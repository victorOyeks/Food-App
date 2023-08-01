package com.example.foodapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

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

    public ApiResponse(ResponseEntity<?> authenticate, String accessToken, String refreshToken) {

    }


//    public ApiResponse(String id, LocalDateTime requestTime, String requestType, String referenceId, boolean status, String message, T data) {
//        this.requestTime = requestTime;
//        this.requestType = requestType;
//        this.referenceId = referenceId;
//        this.status = status;
//        this.message = message;
//        this.data = data;
//    }
//
//    public ApiResponse(String id, LocalDateTime requestTime, String requestType, String referenceId, boolean status, String message, String accessToken, String refreshToken, T data) {
//        this.requestTime = requestTime;
//        this.requestType = requestType;
//        this.referenceId = referenceId;
//        this.status = status;
//        this.message = message;
//        this.accessToken = accessToken;
//        this.refreshToken = refreshToken;
//        this.data = data;
//    }
//
//    public ApiResponse(T data) {
//        this.data = data;
////        this.message = message;
//    }

}
