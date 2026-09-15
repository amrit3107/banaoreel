package com.banaoreel.backend.controller;

import com.banaoreel.backend.dto.JobCompleteRequest;
import com.banaoreel.backend.dto.JobFailRequest;
import com.banaoreel.backend.entity.JobStatus;
import com.banaoreel.backend.service.VideoJobService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Endpoints called only by the Node worker service, never by the Android app.
 * Protected by a shared secret header rather than user JWT auth.
 * TODO: move this to a separate internal network / VPC-only route in production
 * rather than relying solely on the shared-secret header.
 */
@RestController
@RequestMapping("/internal/videos")
public class InternalWorkerController {

    private final VideoJobService videoJobService;
    private final String internalApiKey;

    public InternalWorkerController(
            VideoJobService videoJobService,
            @Value("${banaoreel.worker.internal-api-key}") String internalApiKey
    ) {
        this.videoJobService = videoJobService;
        this.internalApiKey = internalApiKey;
    }

    private void checkAuth(String providedKey) {
        if (providedKey == null || !providedKey.equals(internalApiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid internal API key");
        }
    }

    @PatchMapping("/{id}/status")
    public void updateStatus(
            @RequestHeader("X-Internal-Key") String key,
            @PathVariable UUID id,
            @RequestParam JobStatus status
    ) {
        checkAuth(key);
        videoJobService.updateStatus(id, status);
    }

    @PostMapping("/{id}/complete")
    public void complete(
            @RequestHeader("X-Internal-Key") String key,
            @PathVariable UUID id,
            @RequestBody JobCompleteRequest request
    ) {
        checkAuth(key);
        videoJobService.markComplete(id, request.getVideoUrl());
    }

    @PostMapping("/{id}/fail")
    public void fail(
            @RequestHeader("X-Internal-Key") String key,
            @PathVariable UUID id,
            @RequestBody JobFailRequest request
    ) {
        checkAuth(key);
        videoJobService.markFailed(id, request.getReason());
    }
}
