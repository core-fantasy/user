package com.corefantasy.user.loginprovider;

import com.corefantasy.user.model.User;

import java.util.Map;

public interface LoginProvider {
    User login(Map<String, Object> credentials) throws LoginProviderException;
    String getName();
}
