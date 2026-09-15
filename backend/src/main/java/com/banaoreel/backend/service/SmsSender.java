package com.banaoreel.backend.service;

public interface SmsSender {
    void send(String phone, String message);
}
