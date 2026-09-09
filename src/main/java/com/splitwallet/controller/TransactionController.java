package com.splitwallet.controller;

import com.splitwallet.dto.response.ApiResponse;
import com.splitwallet.dto.response.TransactionResponse;
import com.splitwallet.service.WalletService;
import com.splitwallet.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final WalletService walletService;
    private final SecurityUtils securityUtils;

    public TransactionController(WalletService walletService, SecurityUtils securityUtils) {
        this.walletService = walletService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions() {
        Long userId = securityUtils.getCurrentUserId();
        List<TransactionResponse> response = walletService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Transaction history retrieved successfully", response));
    }
}
