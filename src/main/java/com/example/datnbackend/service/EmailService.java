package com.example.datnbackend.service;

public interface EmailService {
    void sendOtpMessage(String to, String subject, String message);
}
