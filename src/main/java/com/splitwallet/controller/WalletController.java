package com.splitwallet.controller;

import com.splitwallet.dto.request.TopUpRequest;
import com.splitwallet.dto.response.ApiResponse;
import com.splitwallet.dto.response.TransactionResponse;
import com.splitwallet.dto.response.WalletResponse;
import com.splitwallet.service.WalletService;
import com.splitwallet.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final SecurityUtils securityUtils;

    public WalletController(WalletService walletService, SecurityUtils securityUtils) {
        this.walletService = walletService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet() {
        Long userId = securityUtils.getCurrentUserId();
        WalletResponse response = walletService.getWalletByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Wallet details retrieved successfully", response));
    }

    @PostMapping("/topup")
    public ResponseEntity<ApiResponse<WalletResponse>> topUpWallet(@Valid @RequestBody TopUpRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        WalletResponse response = walletService.topUpWallet(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Wallet topped up successfully", response));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getWalletTransactions() {
        Long userId = securityUtils.getCurrentUserId();
        List<TransactionResponse> response = walletService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Wallet transactions retrieved successfully", response));
    }
}
