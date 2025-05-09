package com.example.foodapp.exception;


import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ResourceCreationException extends RuntimeException{
    protected String message;
    public ResourceCreationException(String message) {
        super(message);
        this.message = message;
    }

}