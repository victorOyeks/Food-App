package com.example.foodapp.service;

import com.example.foodapp.dto.request.EmailDetails;

import java.io.IOException;

public interface EmailService {
    void sendEmail(EmailDetails emailDetails) throws IOException;
}
