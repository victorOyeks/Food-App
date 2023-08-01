package com.example.foodapp.service.impl;

import com.example.foodapp.dto.request.EmailDetails;
import com.example.foodapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSeder;

    @Value("${spring.mail.username}")
    private String senderEmail;

//    @Async
    @Override
    public void sendEmail(EmailDetails emailDetails) {

        try {
            log.info("sending email to {}", emailDetails.getRecipient());
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(emailDetails.getRecipient());
            mailMessage.setText(emailDetails.getMessageBody());
            mailMessage.setSubject(emailDetails.getSubject());

            mailSeder.send(mailMessage);
            System.out.println("mail sent successfully");
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }
}