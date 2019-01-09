package com.corefantasy.user.loginprovider;

import com.corefantasy.user.model.User;

public interface LoginProvider {
    User login(Object credentials) throws LoginProviderException;
    String getName();
}
