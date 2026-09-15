package com.banaoreel.backend.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Default SMS sender for local/dev: just logs the OTP instead of sending a real text.
 * Active whenever no other SmsSender bean (e.g. Msg91SmsSender) is registered.
 * Swap this for a real provider before shipping — see Msg91SmsSender for the pattern.
 */
@Component
public class LoggingSmsSender implements SmsSender {
    @Override
    public void send(String phone, String message) {
        System.out.println("[dev-sms] to " + phone + ": " + message);
    }
}
