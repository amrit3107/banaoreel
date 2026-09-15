package com.banaoreel.backend.service;

import com.banaoreel.backend.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Sends the "your video is ready" / "generation failed" push via Firebase
 * Cloud Messaging. Requires a Firebase service account JSON configured for
 * FirebaseApp (see config/FirebaseConfig.java) and the app's FCM token stored
 * against the user (set via POST /users/fcm-token, called from
 * BanaoReelMessagingService.onNewToken on the Android side).
 */
@Service
public class PushNotificationService {

    private final UserRepository userRepository;

    public PushNotificationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void notifyJobComplete(UUID userId) {
        send(userId, "Your video is ready", "Tap to view and share it.");
    }

    public void notifyJobFailed(UUID userId) {
        send(userId, "Generation failed", "The amount was refunded to your wallet.");
    }

    private void send(UUID userId, String title, String body) {
        userRepository.findById(userId).ifPresent(user -> {
            String token = user.getFcmToken();
            if (token == null || token.isBlank()) return; // no device registered yet

            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder().setTitle(title).setBody(body).build())
                    .build();

            try {
                FirebaseMessaging.getInstance().send(message);
            } catch (Exception e) {
                // Don't let a push failure fail the job-completion transaction it's called from.
                System.err.println("Failed to send push to user " + userId + ": " + e.getMessage());
            }
        });
    }
}
