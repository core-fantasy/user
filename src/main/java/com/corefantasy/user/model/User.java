package com.corefantasy.user.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String id;

    @NotNull
    @Column
    private String email;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    @ElementCollection
    private List<String> roles = new ArrayList<>();

    public User() {}

    public User(String id, String name, String email, List<String> roles) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User: {id = " + id +
                ", name = '" + name + "'" +
                ", email = '" + email + "'" +
                ", roles: [" + String.join(",", roles) + "]}";
    }
}
