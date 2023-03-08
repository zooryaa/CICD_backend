package com.example.demo.core.exception;

/**
 * This exception gets thrown if in the {@link com.example.demo.core.generic.StatusOr} getItem method is accessed
 * without calling isOkAndPresent().
 */
public class NotCheckedException extends Exception {
    public NotCheckedException(String message) {
        super(message);
    }
}
