package com.splitwallet.controller;

import com.splitwallet.dto.response.ApiResponse;
import com.splitwallet.dto.response.SplitParticipantResponse;
import com.splitwallet.service.SplitService;
import com.splitwallet.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/split-requests")
public class SplitRequestController {

    private final SplitService splitService;
    private final SecurityUtils securityUtils;

    public SplitRequestController(SplitService splitService, SecurityUtils securityUtils) {
        this.splitService = splitService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SplitParticipantResponse>>> getIncomingRequests() {
        Long userId = securityUtils.getCurrentUserId();
        List<SplitParticipantResponse> response = splitService.getIncomingRequests(userId);
        return ResponseEntity.ok(ApiResponse.success("Incoming split requests retrieved successfully", response));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<SplitParticipantResponse>> approveRequest(@PathVariable("id") Long participantId) {
        Long userId = securityUtils.getCurrentUserId();
        SplitParticipantResponse response = splitService.approveSplitRequest(userId, participantId);
        return ResponseEntity.ok(ApiResponse.success("Split request approved and debited successfully", response));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<SplitParticipantResponse>> rejectRequest(@PathVariable("id") Long participantId) {
        Long userId = securityUtils.getCurrentUserId();
        SplitParticipantResponse response = splitService.rejectSplitRequest(userId, participantId);
        return ResponseEntity.ok(ApiResponse.success("Split request rejected", response));
    }
}
