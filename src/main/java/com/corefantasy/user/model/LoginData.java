package com.corefantasy.user.model;

public class LoginData {
    private String jwt;
    private boolean newUser;

    public LoginData(String jwt, boolean newUser) {
        this.jwt = jwt;
        this.newUser = newUser;
    }

    public String getJwt() {
        return jwt;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }
}
