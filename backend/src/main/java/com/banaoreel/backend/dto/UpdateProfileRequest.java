package com.banaoreel.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateProfileRequest {
    @NotBlank
    private String name;

    private String gender; // MALE | FEMALE | OTHER | PREFER_NOT_TO_SAY, optional

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
