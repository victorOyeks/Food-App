package com.example.foodapp.exception;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException (String message) {
        super(message);
    }
}