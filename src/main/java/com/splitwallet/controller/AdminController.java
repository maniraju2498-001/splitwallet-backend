package com.splitwallet.controller;

import com.splitwallet.dto.response.AdminStatsResponse;
import com.splitwallet.dto.response.ApiResponse;
import com.splitwallet.entity.ApprovalStatus;
import com.splitwallet.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final ExpenseSplitRepository splitRepository;
    private final TransactionRepository transactionRepository;
    private final SplitParticipantRepository participantRepository;

    public AdminController(
            UserRepository userRepository,
            WalletRepository walletRepository,
            ExpenseSplitRepository splitRepository,
            TransactionRepository transactionRepository,
            SplitParticipantRepository participantRepository
    ) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.splitRepository = splitRepository;
        this.transactionRepository = transactionRepository;
        this.participantRepository = participantRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getStats() {
        long pendingRequests = participantRepository.findAll().stream()
                .filter(p -> p.getApprovalStatus() == ApprovalStatus.PENDING)
                .count();

        AdminStatsResponse stats = AdminStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalWallets(walletRepository.count())
                .totalSplits(splitRepository.count())
                .totalTransactions(transactionRepository.count())
                .pendingRequests(pendingRequests)
                .build();

        return ResponseEntity.ok(ApiResponse.success("Admin stats retrieved", stats));
    }
}
