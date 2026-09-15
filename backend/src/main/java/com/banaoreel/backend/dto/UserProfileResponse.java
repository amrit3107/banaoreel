package com.banaoreel.backend.dto;

public class UserProfileResponse {
    private String id;
    private String phone;
    private String name;
    private String gender; // null if not set

    public UserProfileResponse(String id, String phone, String name, String gender) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.gender = gender;
    }

    public String getId() { return id; }
    public String getPhone() { return phone; }
    public String getName() { return name; }
    public String getGender() { return gender; }
}
