package com.corefantasy.user.controller.commands;

import java.util.ArrayList;
import java.util.List;

public class RegisteredUser {
    private String id;
    private List<String> roles = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "RegisteredUser(id: " + getId() + ", roles: [" + String.join(", ", getRoles()) + "])";
    }
}
