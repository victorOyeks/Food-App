package com.example.foodapp.service;

import com.example.foodapp.payloads.request.EmailDetails;

import java.io.IOException;

public interface EmailService {
    void sendEmail(EmailDetails emailDetails) throws IOException;
}
