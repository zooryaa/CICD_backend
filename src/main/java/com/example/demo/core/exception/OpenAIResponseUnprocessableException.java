package com.example.demo.core.exception;

public class OpenAIResponseUnprocessableException extends Exception {
    public OpenAIResponseUnprocessableException(String message) {
        super(message);
    }
}
