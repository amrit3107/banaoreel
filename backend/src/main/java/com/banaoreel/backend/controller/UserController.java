package com.banaoreel.backend.controller;

import com.banaoreel.backend.repository.UserRepository;
import com.banaoreel.backend.security.CurrentUser;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Called from Android's BanaoReelMessagingService.onNewToken. */
    @PostMapping("/fcm-token")
    public void updateFcmToken(@RequestBody Map<String, String> body) {
        userRepository.findById(CurrentUser.id()).ifPresent(user -> {
            user.setFcmToken(body.get("token"));
            userRepository.save(user);
        });
    }
}
