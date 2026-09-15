package com.banaoreel.backend.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory OTP store: fine for a single-instance deployment. If the backend
 * ever runs on multiple instances, move this to Redis so OTPs are shared
 * across instances (TODO before horizontal scaling).
 */
@Service
public class OtpService {

    private record OtpEntry(String code, Instant expiresAt) {}

    private final ConcurrentHashMap<String, OtpEntry> store = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final SmsSender smsSender;

    private static final int OTP_LENGTH = 6;
    private static final long TTL_SECONDS = 300; // 5 minutes

    public OtpService(SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    public void sendOtp(String phone) {
        String code = String.format("%0" + OTP_LENGTH + "d", random.nextInt((int) Math.pow(10, OTP_LENGTH)));
        store.put(phone, new OtpEntry(code, Instant.now().plusSeconds(TTL_SECONDS)));
        smsSender.send(phone, "Your BanaoReel verification code is " + code);
    }

    public boolean verifyOtp(String phone, String submittedCode) {
        OtpEntry entry = store.get(phone);
        if (entry == null) return false;
        if (Instant.now().isAfter(entry.expiresAt())) {
            store.remove(phone);
            return false;
        }
        boolean matches = entry.code().equals(submittedCode);
        if (matches) store.remove(phone); // one-time use
        return matches;
    }
}
