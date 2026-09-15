package com.banaoreel.backend.controller;

import com.banaoreel.backend.dto.UpdateProfileRequest;
import com.banaoreel.backend.dto.UserProfileResponse;
import com.banaoreel.backend.entity.Gender;
import com.banaoreel.backend.entity.User;
import com.banaoreel.backend.repository.UserRepository;
import com.banaoreel.backend.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public UserProfileResponse getMe() {
        User user = userRepository.findById(CurrentUser.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toResponse(user);
    }

    /** Called once at the end of onboarding, and reusable later for profile edits. */
    @PatchMapping("/me")
    public UserProfileResponse updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        User user = userRepository.findById(CurrentUser.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setName(request.getName());
        if (request.getGender() != null && !request.getGender().isBlank()) {
            try {
                user.setGender(Gender.valueOf(request.getGender()));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid gender value");
            }
        }
        userRepository.save(user);
        return toResponse(user);
    }

    /** Called from Android's BanaoReelMessagingService.onNewToken. */
    @PostMapping("/fcm-token")
    public void updateFcmToken(@RequestBody Map<String, String> body) {
        userRepository.findById(CurrentUser.id()).ifPresent(user -> {
            user.setFcmToken(body.get("token"));
            userRepository.save(user);
        });
    }

    private UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(
                user.getId().toString(),
                user.getPhone(),
                user.getName(),
                user.getGender() != null ? user.getGender().name() : null
        );
    }
}
