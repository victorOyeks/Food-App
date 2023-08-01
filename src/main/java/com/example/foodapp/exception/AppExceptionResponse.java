package com.example.foodapp.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AppExceptionResponse <T>{
    private LocalDateTime requestTime;
    private String requestType;
    private String referenceId;
    private Boolean status;
    private String message;
    private T data;
}