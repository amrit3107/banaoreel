package com.banaoreel.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

/**
 * Real MSG91 integration (common choice for Indian OTP SMS — cheap, good delivery rates).
 * Not registered as a @Component by default: this project ships with LoggingSmsSender
 * active for local dev. To go live, add @Component here (and remove it from
 * LoggingSmsSender, since Spring needs exactly one SmsSender bean) and set
 * banaoreel.sms.msg91-auth-key + banaoreel.sms.msg91-template-id in application.yml.
 */
public class Msg91SmsSender implements SmsSender {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String authKey;
    private final String templateId;

    public Msg91SmsSender(
            @Value("${banaoreel.sms.msg91-auth-key:}") String authKey,
            @Value("${banaoreel.sms.msg91-template-id:}") String templateId
    ) {
        this.authKey = authKey;
        this.templateId = templateId;
    }

    @Override
    public void send(String phone, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("authkey", authKey);
        headers.set("Content-Type", "application/json");

        String url = "https://control.msg91.com/api/v5/otp?template_id=" + templateId
                + "&mobile=" + phone + "&otp=" + message;

        restTemplate.exchange(url, org.springframework.http.HttpMethod.POST, new HttpEntity<>(headers), String.class);
    }
}
