package com.banaoreel.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "video_jobs")
public class VideoJob {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 500)
    private String prompt;

    @Column(nullable = false)
    private int durationSec;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status = JobStatus.QUEUED;

    private String videoUrl;

    @Column(nullable = false)
    private long costPaise;

    private String failureReason;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
    public int getDurationSec() { return durationSec; }
    public void setDurationSec(int durationSec) { this.durationSec = durationSec; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public long getCostPaise() { return costPaise; }
    public void setCostPaise(long costPaise) { this.costPaise = costPaise; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    public Instant getCreatedAt() { return createdAt; }
}
