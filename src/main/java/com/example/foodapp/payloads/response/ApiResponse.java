package com.example.foodapp.payloads.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private LocalDateTime requestTime = LocalDateTime.now();
    private String requestType = "Outbound";
    private String referenceId = UUID.randomUUID().toString();
    private Boolean status;
    private String message;
    private T data;

    public ApiResponse(T data) {
        this.requestTime = LocalDateTime.now();
        this.requestType = "Outbound";
        this.status = true;
        this.message = "Processed Successfully";
        this.data = data;
    }
}