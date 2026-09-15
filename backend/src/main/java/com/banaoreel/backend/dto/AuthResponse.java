package com.banaoreel.backend.dto;

public class AuthResponse {
    private String token;
    private String userId;
    private String name;      // null if the user hasn't completed onboarding yet
    private boolean profileComplete;

    public AuthResponse(String token, String userId, String name, boolean profileComplete) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.profileComplete = profileComplete;
    }

    public String getToken() { return token; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public boolean isProfileComplete() { return profileComplete; }
}
