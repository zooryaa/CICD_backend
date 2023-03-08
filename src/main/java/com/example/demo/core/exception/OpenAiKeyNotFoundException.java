package com.example.demo.core.exception;

public class OpenAiKeyNotFoundException extends RuntimeException {
    public OpenAiKeyNotFoundException(String message) {
        super(message);
    }
}
