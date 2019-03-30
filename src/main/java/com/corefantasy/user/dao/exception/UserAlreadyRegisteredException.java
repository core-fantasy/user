package com.corefantasy.user.dao.exception;

public class UserAlreadyRegisteredException extends RuntimeException {
    public UserAlreadyRegisteredException(String message) {
        super(message);
    }

    public UserAlreadyRegisteredException(String message, Exception cause) {
        super(message, cause);
    }
}
