package com.corefantasy.user.controller;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Exception suppressed) {
        super(message, suppressed);
    }
}
