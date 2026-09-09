package com.splitwallet.service;

import com.splitwallet.dto.request.CreateSplitRequest;
import com.splitwallet.dto.request.ParticipantShareRequest;
import com.splitwallet.dto.response.SplitParticipantResponse;
import com.splitwallet.dto.response.SplitResponse;
import com.splitwallet.entity.*;
import com.splitwallet.exception.*;
import com.splitwallet.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class SplitService {

    private final ExpenseSplitRepository splitRepository;
    private final SplitParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;

    public SplitService(
            ExpenseSplitRepository splitRepository,
            SplitParticipantRepository participantRepository,
            UserRepository userRepository,
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            NotificationService notificationService
    ) {
        this.splitRepository = splitRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public SplitResponse createSplit(Long creatorId, CreateSplitRequest request) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found with id: " + creatorId));

        // 1. Validate sum of participant shares equals totalAmount
        BigDecimal sumOfShares = request.getParticipants().stream()
                .map(ParticipantShareRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumOfShares.compareTo(request.getTotalAmount()) != 0) {
            throw new InvalidSplitAmountException(
                    String.format("Sum of individual shares (₹%s) does not equal total expense amount (₹%s)",
                            sumOfShares, request.getTotalAmount())
            );
        }

        // 2. Validate all participants exist
        for (ParticipantShareRequest pReq : request.getParticipants()) {
            if (!userRepository.existsById(pReq.getUserId())) {
                throw new ResourceNotFoundException("Participant user not found with id: " + pReq.getUserId());
            }
        }

        // 3. Save ExpenseSplit Header
        ExpenseSplit split = ExpenseSplit.builder()
                .creatorId(creatorId)
                .title(request.getTitle())
                .totalAmount(request.getTotalAmount())
                .description(request.getDescription())
                .status(SplitStatus.ACTIVE)
                .build();
        ExpenseSplit savedSplit = splitRepository.save(split);

        // 4. Save Participants & Send Notifications
        List<SplitParticipant> participants = request.getParticipants().stream().map(pReq -> {
            boolean isCreator = pReq.getUserId().equals(creatorId);
            SplitParticipant participant = SplitParticipant.builder()
                    .splitId(savedSplit.getId())
                    .userId(pReq.getUserId())
                    .amount(pReq.getAmount())
                    .approvalStatus(isCreator ? ApprovalStatus.DEBITED : ApprovalStatus.PENDING)
                    .debited(isCreator)
                    .approvedAt(isCreator ? LocalDateTime.now() : null)
                    .build();

            SplitParticipant savedPart = participantRepository.save(participant);

            if (!isCreator) {
                notificationService.createNotification(
                        pReq.getUserId(),
                        "New Split Request",
                        String.format("%s requested ₹%s for \"%s\"", creator.getFullName(), pReq.getAmount(), request.getTitle()),
                        NotificationType.REQUEST
                );
            }

            return savedPart;
        }).toList();

        return mapToSplitResponse(savedSplit, creator.getFullName(), participants);
    }

    public List<SplitResponse> getMyCreatedSplits(Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + creatorId));

        return splitRepository.findByCreatorIdOrderByCreatedAtDesc(creatorId)
                .stream()
                .map(split -> {
                    List<SplitParticipant> parts = participantRepository.findBySplitId(split.getId());
                    return mapToSplitResponse(split, creator.getFullName(), parts);
                })
                .toList();
    }

    public SplitResponse getSplitById(Long userId, Long splitId) {
        ExpenseSplit split = splitRepository.findById(splitId)
                .orElseThrow(() -> new ResourceNotFoundException("Split not found with id: " + splitId));

        List<SplitParticipant> parts = participantRepository.findBySplitId(splitId);
        boolean isCreator = split.getCreatorId().equals(userId);
        boolean isParticipant = parts.stream().anyMatch(p -> p.getUserId().equals(userId));

        if (!isCreator && !isParticipant) {
            throw new UnauthorizedAccessException("You are not authorized to view this expense split");
        }

        User creator = userRepository.findById(split.getCreatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found"));

        return mapToSplitResponse(split, creator.getFullName(), parts);
    }

    public List<SplitParticipantResponse> getIncomingRequests(Long userId) {
        return participantRepository.findByUserIdOrderByApprovalStatusAsc(userId)
                .stream()
                .map(this::mapToSplitParticipantResponse)
                .toList();
    }

    @Transactional
    public SplitParticipantResponse approveSplitRequest(Long userId, Long participantId) {
        SplitParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Split request not found with id: " + participantId));

        // Security check
        if (!participant.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You cannot approve a split request assigned to another user");
        }

        if (participant.getApprovalStatus() == ApprovalStatus.DEBITED || participant.getApprovalStatus() == ApprovalStatus.APPROVED) {
            throw new BadRequestException("This split request has already been approved and debited");
        }
        if (participant.getApprovalStatus() == ApprovalStatus.REJECTED) {
            throw new BadRequestException("This split request was previously rejected");
        }

        ExpenseSplit split = splitRepository.findById(participant.getSplitId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense split not found"));

        // Wallet Balance Check
        Wallet participantWallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));

        if (participantWallet.getBalance().compareTo(participant.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    String.format("Insufficient wallet balance (Available: ₹%s). Required: ₹%s. Please top up your wallet.",
                            participantWallet.getBalance(), participant.getAmount())
            );
        }

        // Debit Participant Wallet
        BigDecimal newParticipantBalance = participantWallet.getBalance().subtract(participant.getAmount());
        participantWallet.setBalance(newParticipantBalance);
        walletRepository.save(participantWallet);

        String refId = "SPL-" + split.getId() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        // Transaction record for Participant (SPLIT_DEBIT)
        Transaction debitTxn = Transaction.builder()
                .userId(userId)
                .transactionType(TransactionType.SPLIT_DEBIT)
                .amount(participant.getAmount())
                .balanceAfter(newParticipantBalance)
                .description("Split Share Paid - " + split.getTitle())
                .referenceId(refId)
                .status("COMPLETED")
                .build();
        transactionRepository.save(debitTxn);

        // Credit Creator Wallet
        Wallet creatorWallet = walletRepository.findByUserId(split.getCreatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Creator wallet not found"));

        BigDecimal newCreatorBalance = creatorWallet.getBalance().add(participant.getAmount());
        creatorWallet.setBalance(newCreatorBalance);
        walletRepository.save(creatorWallet);

        // Transaction record for Creator (SPLIT_CREDIT)
        User participantUser = userRepository.findById(userId).orElse(null);
        String participantName = participantUser != null ? participantUser.getFullName() : "User " + userId;

        Transaction creditTxn = Transaction.builder()
                .userId(split.getCreatorId())
                .transactionType(TransactionType.SPLIT_CREDIT)
                .amount(participant.getAmount())
                .balanceAfter(newCreatorBalance)
                .description("Split Share Received from " + participantName + " (" + split.getTitle() + ")")
                .referenceId(refId)
                .status("COMPLETED")
                .build();
        transactionRepository.save(creditTxn);

        // Update Participant Status
        participant.setApprovalStatus(ApprovalStatus.DEBITED);
        participant.setDebited(true);
        participant.setApprovedAt(LocalDateTime.now());
        SplitParticipant updatedParticipant = participantRepository.save(participant);

        // Send Notifications
        notificationService.createNotification(
                split.getCreatorId(),
                "Split Request Approved",
                String.format("%s approved & paid ₹%s for \"%s\"", participantName, participant.getAmount(), split.getTitle()),
                NotificationType.APPROVAL
        );

        notificationService.createNotification(
                userId,
                "Successful Debit",
                String.format("₹%s debited for \"%s\"", participant.getAmount(), split.getTitle()),
                NotificationType.DEBIT
        );

        return mapToSplitParticipantResponse(updatedParticipant);
    }

    @Transactional
    public SplitParticipantResponse rejectSplitRequest(Long userId, Long participantId) {
        SplitParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Split request not found with id: " + participantId));

        if (!participant.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You cannot reject a split request assigned to another user");
        }

        if (participant.getApprovalStatus() == ApprovalStatus.DEBITED || participant.getApprovalStatus() == ApprovalStatus.APPROVED) {
            throw new BadRequestException("Cannot reject an already approved request");
        }

        ExpenseSplit split = splitRepository.findById(participant.getSplitId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense split not found"));

        participant.setApprovalStatus(ApprovalStatus.REJECTED);
        participant.setRejectedAt(LocalDateTime.now());
        SplitParticipant updatedParticipant = participantRepository.save(participant);

        User participantUser = userRepository.findById(userId).orElse(null);
        String participantName = participantUser != null ? participantUser.getFullName() : "User " + userId;

        notificationService.createNotification(
                split.getCreatorId(),
                "Split Request Rejected",
                String.format("%s rejected your split request for \"%s\"", participantName, split.getTitle()),
                NotificationType.REJECTION
        );

        return mapToSplitParticipantResponse(updatedParticipant);
    }

    private SplitResponse mapToSplitResponse(ExpenseSplit split, String creatorName, List<SplitParticipant> participants) {
        List<SplitParticipantResponse> pResponses = participants.stream()
                .map(this::mapToSplitParticipantResponse)
                .toList();

        return SplitResponse.builder()
                .id(split.getId())
                .creatorId(split.getCreatorId())
                .creatorName(creatorName)
                .title(split.getTitle())
                .totalAmount(split.getTotalAmount())
                .description(split.getDescription())
                .status(split.getStatus())
                .createdAt(split.getCreatedAt())
                .participants(pResponses)
                .build();
    }

    private SplitParticipantResponse mapToSplitParticipantResponse(SplitParticipant p) {
        User u = userRepository.findById(p.getUserId()).orElse(null);
        ExpenseSplit split = splitRepository.findById(p.getSplitId()).orElse(null);
        User creator = split != null ? userRepository.findById(split.getCreatorId()).orElse(null) : null;
        return SplitParticipantResponse.builder()
                .id(p.getId())
                .splitId(p.getSplitId())
                .userId(p.getUserId())
                .userName(u != null ? u.getFullName() : "User " + p.getUserId())
                .userEmail(u != null ? u.getEmail() : "")
                .splitTitle(split != null ? split.getTitle() : "")
                .splitDescription(split != null ? split.getDescription() : "")
                .creatorName(creator != null ? creator.getFullName() : "Unknown")
                .amount(p.getAmount())
                .approvalStatus(p.getApprovalStatus())
                .debited(p.getDebited())
                .approvedAt(p.getApprovedAt())
                .rejectedAt(p.getRejectedAt())
                .build();
    }
}
