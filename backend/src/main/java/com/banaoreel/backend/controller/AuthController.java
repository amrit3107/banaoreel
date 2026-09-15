package com.banaoreel.backend.controller;

import com.banaoreel.backend.entity.User;
import com.banaoreel.backend.repository.UserRepository;
import com.banaoreel.backend.security.JwtService;
import com.banaoreel.backend.service.OtpService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, OtpService otpService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.jwtService = jwtService;
    }

    @PostMapping("/otp/request")
    public void requestOtp(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("phone is required");
        }
        otpService.sendOtp(phone);
    }

    @PostMapping("/otp/verify")
    public Map<String, String> verifyOtp(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        String otp = body.get("otp");

        if (!otpService.verifyOtp(phone, otp)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        User user = userRepository.findByPhone(phone).orElseGet(() -> {
            User u = new User();
            u.setPhone(phone);
            return userRepository.save(u);
        });

        String token = jwtService.issueToken(user.getId());
        return Map.of("token", token, "userId", user.getId().toString());
    }
}
