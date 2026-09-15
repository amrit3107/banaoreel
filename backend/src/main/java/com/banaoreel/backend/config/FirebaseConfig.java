package com.banaoreel.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * Initializes the Firebase Admin SDK on startup so PushNotificationService can
 * send FCM messages. Place your Firebase service account JSON (downloaded from
 * Firebase Console -> Project Settings -> Service Accounts) at the configured
 * classpath location before this will work; without it, push sending will fail
 * silently (caught in PushNotificationService) and everything else keeps working.
 */
@Component
public class FirebaseConfig {

    @Value("${banaoreel.firebase.service-account-path:firebase-service-account.json}")
    private String serviceAccountPath;

    @PostConstruct
    public void init() {
        try (InputStream serviceAccount = new ClassPathResource(serviceAccountPath).getInputStream()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            System.err.println("Firebase not initialized (service account file missing): " + e.getMessage());
        }
    }
}
