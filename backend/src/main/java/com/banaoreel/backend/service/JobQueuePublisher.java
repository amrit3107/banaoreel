package com.banaoreel.backend.service;

import org.springframework.stereotype.Component;
import java.util.UUID;

/**
 * Pushes a job id onto the queue that the Node worker service consumes from.
 * TODO: replace this stub with an actual SQS (or Redis/BullMQ-compatible) client.
 * Kept as its own component so the queue tech can change without touching VideoJobService.
 */
@Component
public class JobQueuePublisher {
    public void publish(UUID jobId) {
        // TODO: sqsClient.sendMessage(queueUrl, jobId.toString());
        System.out.println("[stub] enqueued job " + jobId);
    }
}
