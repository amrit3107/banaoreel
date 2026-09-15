package com.banaoreel.backend.service;

import com.banaoreel.backend.entity.VideoJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Map;

@Component
public class JobQueuePublisher {

    private final SqsClient sqsClient;
    private final String queueUrl;

    public JobQueuePublisher(
            SqsClient sqsClient,
            @Value("${banaoreel.worker.queue-url}") String queueUrl
    ) {
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
    }

    /** Message shape must match what worker/src/index.js expects to parse. */
    public void publish(VideoJob job) {
        String body = String.format(
                "{\"jobId\":\"%s\",\"prompt\":%s,\"durationSec\":%d}",
                job.getId(),
                toJsonString(job.getPrompt()),
                job.getDurationSec()
        );

        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(body)
                .build());
    }

    private String toJsonString(String raw) {
        // Minimal manual escaping to avoid pulling in a JSON lib just for this.
        return "\"" + raw.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }
}
