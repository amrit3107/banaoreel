package com.banaoreel.backend.controller;

import com.banaoreel.backend.entity.User;
import com.banaoreel.backend.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * SCAFFOLD ONLY. Real OTP sending (via an SMS provider) and JWT issuance
 * are not implemented yet — wire Firebase Phone Auth or an SMS gateway
 * (e.g. MSG91, Twilio) here, plus real JWT signing using the
 * banaoreel.jwt.secret config value.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/otp/request")
    public void requestOtp(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        // TODO: call SMS provider to send OTP, store OTP+expiry (e.g. in Redis) keyed by phone
        System.out.println("[stub] OTP requested for " + phone);
    }

    @PostMapping("/otp/verify")
    public Map<String, String> verifyOtp(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        // TODO: actually validate the OTP against what was stored/sent
        User user = userRepository.findByPhone(phone).orElseGet(() -> {
            User u = new User();
            u.setPhone(phone);
            return userRepository.save(u);
        });
        // TODO: issue a real signed JWT here instead of the raw user id
        return Map.of("token", user.getId().toString(), "userId", user.getId().toString());
    }
}
