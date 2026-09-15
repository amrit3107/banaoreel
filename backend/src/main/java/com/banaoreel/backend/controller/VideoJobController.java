package com.banaoreel.backend.controller;

import com.banaoreel.backend.dto.CreateVideoRequest;
import com.banaoreel.backend.entity.VideoJob;
import com.banaoreel.backend.service.InsufficientBalanceException;
import com.banaoreel.backend.service.VideoJobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/videos")
public class VideoJobController {

    private final VideoJobService videoJobService;

    public VideoJobController(VideoJobService videoJobService) {
        this.videoJobService = videoJobService;
    }

    @PostMapping
    public VideoJob createVideo(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateVideoRequest request
    ) {
        try {
            return videoJobService.createJob(userId, request.getPrompt(), request.getDurationSec());
        } catch (InsufficientBalanceException e) {
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public VideoJob getVideo(@RequestHeader("X-User-Id") UUID userId, @PathVariable UUID id) {
        return videoJobService.getJob(id, userId);
    }

    @GetMapping
    public List<VideoJob> listVideos(@RequestHeader("X-User-Id") UUID userId) {
        return videoJobService.listJobs(userId);
    }
}
