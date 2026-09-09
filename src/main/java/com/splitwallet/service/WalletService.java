package com.splitwallet.service;

import com.splitwallet.dto.request.TopUpRequest;
import com.splitwallet.dto.response.*;
import com.splitwallet.entity.*;
import com.splitwallet.exception.ResourceNotFoundException;
import com.splitwallet.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final ExpenseSplitRepository splitRepository;
    private final SplitParticipantRepository participantRepository;
    private final UserRepository userRepository;

    public WalletService(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            NotificationService notificationService,
            ExpenseSplitRepository splitRepository,
            SplitParticipantRepository participantRepository,
            UserRepository userRepository
    ) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
        this.splitRepository = splitRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
    }

    public WalletResponse getWalletByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for userId: " + userId));
        return WalletResponse.builder()
                .id(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }

    @Transactional
    public WalletResponse topUpWallet(Long userId, TopUpRequest request) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for userId: " + userId));

        BigDecimal newBalance = wallet.getBalance().add(request.getAmount());
        wallet.setBalance(newBalance);
        Wallet savedWallet = walletRepository.save(wallet);

        String refId = "TOPUP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String method = request.getPaymentMethod() != null ? request.getPaymentMethod() : "UPI / Card";

        Transaction transaction = Transaction.builder()
                .userId(userId)
                .transactionType(TransactionType.TOP_UP)
                .amount(request.getAmount())
                .balanceAfter(newBalance)
                .description("Wallet Top Up via " + method)
                .referenceId(refId)
                .status("COMPLETED")
                .build();
        transactionRepository.save(transaction);

        notificationService.createNotification(
                userId,
                "Successful Top-Up",
                "₹" + request.getAmount() + " added to your SplitWallet balance successfully.",
                NotificationType.TOP_UP
        );

        return WalletResponse.builder()
                .id(savedWallet.getId())
                .userId(savedWallet.getUserId())
                .balance(savedWallet.getBalance())
                .updatedAt(savedWallet.getUpdatedAt())
                .build();
    }

    public List<TransactionResponse> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(t -> TransactionResponse.builder()
                        .id(t.getId())
                        .userId(t.getUserId())
                        .transactionType(t.getTransactionType())
                        .amount(t.getAmount())
                        .balanceAfter(t.getBalanceAfter())
                        .description(t.getDescription())
                        .referenceId(t.getReferenceId())
                        .status(t.getStatus())
                        .createdAt(t.getCreatedAt())
                        .build())
                .toList();
    }

    public DashboardMetricsResponse getDashboardMetrics(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for userId: " + userId));

        List<Transaction> allTxns = transactionRepository.findByUserIdOrderByCreatedAtDesc(userId);

        BigDecimal totalAdded = allTxns.stream()
                .filter(t -> t.getTransactionType() == TransactionType.TOP_UP)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDebited = allTxns.stream()
                .filter(t -> t.getTransactionType() == TransactionType.SPLIT_DEBIT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total split amount = sum of all splits created by user
        List<ExpenseSplit> mySplits = splitRepository.findByCreatorIdOrderByCreatedAtDesc(userId);
        BigDecimal totalSplitAmount = mySplits.stream()
                .map(ExpenseSplit::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingRequests = participantRepository.findByUserIdOrderByApprovalStatusAsc(userId)
                .stream()
                .filter(p -> p.getApprovalStatus() == ApprovalStatus.PENDING)
                .count();

        List<TransactionResponse> recentTxns = allTxns.stream()
                .limit(5)
                .map(t -> TransactionResponse.builder()
                        .id(t.getId())
                        .userId(t.getUserId())
                        .transactionType(t.getTransactionType())
                        .amount(t.getAmount())
                        .balanceAfter(t.getBalanceAfter())
                        .description(t.getDescription())
                        .referenceId(t.getReferenceId())
                        .status(t.getStatus())
                        .createdAt(t.getCreatedAt())
                        .build())
                .toList();

        // Get recent splits with participant data (reuse mapping from SplitService)
        List<SplitResponse> recentSplits = mySplits.stream()
                .limit(5)
                .map(split -> {
                    var parts = participantRepository.findBySplitId(split.getId());
                    List<SplitParticipantResponse> pResponses = parts.stream()
                            .map(p -> {
                                User u = userRepository.findById(p.getUserId()).orElse(null);
                                return SplitParticipantResponse.builder()
                                        .id(p.getId())
                                        .splitId(p.getSplitId())
                                        .userId(p.getUserId())
                                        .userName(u != null ? u.getFullName() : "User " + p.getUserId())
                                        .userEmail(u != null ? u.getEmail() : "")
                                        .amount(p.getAmount())
                                        .approvalStatus(p.getApprovalStatus())
                                        .debited(p.getDebited())
                                        .approvedAt(p.getApprovedAt())
                                        .rejectedAt(p.getRejectedAt())
                                        .build();
                            }).toList();

                    User creator = userRepository.findById(split.getCreatorId()).orElse(null);
                    return SplitResponse.builder()
                            .id(split.getId())
                            .creatorId(split.getCreatorId())
                            .creatorName(creator != null ? creator.getFullName() : "Unknown")
                            .title(split.getTitle())
                            .totalAmount(split.getTotalAmount())
                            .description(split.getDescription())
                            .status(split.getStatus())
                            .createdAt(split.getCreatedAt())
                            .participants(pResponses)
                            .build();
                })
                .toList();

        return DashboardMetricsResponse.builder()
                .currentBalance(wallet.getBalance())
                .totalMoneyAdded(totalAdded)
                .totalSplitAmount(totalSplitAmount)
                .totalAmountDebited(totalDebited)
                .pendingRequestsCount(pendingRequests)
                .recentTransactions(recentTxns)
                .recentSplits(recentSplits)
                .build();
    }
}
