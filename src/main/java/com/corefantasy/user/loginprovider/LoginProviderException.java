package com.corefantasy.user.loginprovider;

public class LoginProviderException extends RuntimeException {
    public LoginProviderException(String message) {
        super(message);
    }

    public LoginProviderException(String message, Exception suppressed) {
        super(message, suppressed);
    }
}
