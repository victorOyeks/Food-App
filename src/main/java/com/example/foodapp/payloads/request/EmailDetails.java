package com.example.foodapp.payloads.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDetails {
    private String subject;
    private String recipient;
    private String cc;
    private String bcc;
    private String messageBody;
    private String attachment;

    public EmailDetails(String recipient, String subject, String messageBody) {
        this.recipient = recipient;
        this.subject = subject;
        this.messageBody = messageBody;
    }
}
