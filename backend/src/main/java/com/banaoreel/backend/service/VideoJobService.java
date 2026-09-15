package com.banaoreel.backend.service;

import com.banaoreel.backend.entity.JobStatus;
import com.banaoreel.backend.entity.VideoJob;
import com.banaoreel.backend.repository.VideoJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class VideoJobService {

    private final VideoJobRepository videoJobRepository;
    private final WalletService walletService;
    private final PricingService pricingService;
    private final JobQueuePublisher jobQueuePublisher;

    public VideoJobService(
            VideoJobRepository videoJobRepository,
            WalletService walletService,
            PricingService pricingService,
            JobQueuePublisher jobQueuePublisher
    ) {
        this.videoJobRepository = videoJobRepository;
        this.walletService = walletService;
        this.pricingService = pricingService;
        this.jobQueuePublisher = jobQueuePublisher;
    }

    /**
     * Wallet debit and job row creation happen in one DB transaction — this is the
     * critical invariant from the plan: never debit without a job existing, never
     * create a job without a successful debit. If pricing/persistence fails after
     * the debit call, the whole transaction rolls back and the debit is undone too.
     */
    @Transactional
    public VideoJob createJob(UUID userId, String prompt, int durationSec) {
        long costPaise = pricingService.costForDurationPaise(durationSec);

        VideoJob job = new VideoJob();
        job.setUserId(userId);
        job.setPrompt(prompt);
        job.setDurationSec(durationSec);
        job.setCostPaise(costPaise);
        job.setStatus(JobStatus.QUEUED);
        job = videoJobRepository.save(job);

        // Debit after save so we have a job id to attach to the transaction record.
        // Both writes are inside this @Transactional method, so a failure here
        // rolls back the job row too — the invariant holds either way.
        walletService.debit(userId, costPaise, job.getId());

        jobQueuePublisher.publish(job.getId());
        return job;
    }

    public VideoJob getJob(UUID jobId, UUID requestingUserId) {
        VideoJob job = videoJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        if (!job.getUserId().equals(requestingUserId)) {
            throw new SecurityException("Job does not belong to this user");
        }
        return job;
    }

    public List<VideoJob> listJobs(UUID userId) {
        return videoJobRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /** Called by the internal worker endpoints as the job progresses. */
    @Transactional
    public void updateStatus(UUID jobId, JobStatus status) {
        VideoJob job = videoJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        job.setStatus(status);
        videoJobRepository.save(job);
    }

    @Transactional
    public void markComplete(UUID jobId, String videoUrl) {
        VideoJob job = videoJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        job.setStatus(JobStatus.DONE);
        job.setVideoUrl(videoUrl);
        videoJobRepository.save(job);
        // TODO: trigger FCM push to the user here
    }

    /** Failure path: mark job failed and auto-refund the wallet, per the product plan. */
    @Transactional
    public void markFailed(UUID jobId, String reason) {
        VideoJob job = videoJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));
        job.setStatus(JobStatus.FAILED);
        job.setFailureReason(reason);
        videoJobRepository.save(job);

        walletService.refund(job.getUserId(), job.getCostPaise(), job.getId());
        // TODO: trigger FCM push to the user here
    }
}
