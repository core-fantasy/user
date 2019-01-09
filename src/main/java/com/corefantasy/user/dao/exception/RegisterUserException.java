package com.corefantasy.user.dao.exception;

public class RegisterUserException extends RuntimeException {
    public RegisterUserException(String message) {
        super(message);
    }

    public RegisterUserException(String message, Exception suppressed) {
        super(message, suppressed);
    }
}
