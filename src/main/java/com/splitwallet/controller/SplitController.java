package com.splitwallet.controller;

import com.splitwallet.dto.request.CreateSplitRequest;
import com.splitwallet.dto.response.ApiResponse;
import com.splitwallet.dto.response.SplitResponse;
import com.splitwallet.service.SplitService;
import com.splitwallet.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/splits")
public class SplitController {

    private final SplitService splitService;
    private final SecurityUtils securityUtils;

    public SplitController(SplitService splitService, SecurityUtils securityUtils) {
        this.splitService = splitService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SplitResponse>> createSplit(@Valid @RequestBody CreateSplitRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        SplitResponse response = splitService.createSplit(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Expense split created successfully", response));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<SplitResponse>>> getMySplits() {
        Long userId = securityUtils.getCurrentUserId();
        List<SplitResponse> response = splitService.getMyCreatedSplits(userId);
        return ResponseEntity.ok(ApiResponse.success("Created splits retrieved successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SplitResponse>> getSplitById(@PathVariable("id") Long splitId) {
        Long userId = securityUtils.getCurrentUserId();
        SplitResponse response = splitService.getSplitById(userId, splitId);
        return ResponseEntity.ok(ApiResponse.success("Split details retrieved successfully", response));
    }
}
