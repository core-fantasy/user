package com.corefantasy.user.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @NotNull
    @Column(nullable = false)
    private Instant dateJoined;

    @EmbeddedId
    private UserId userId = new UserId();

    @NotNull
    @Column
    private String email;

    @NotNull
    @Column(nullable = false)
    private String name;

    @NotNull
    private Preferences preferences = new Preferences();

    @NotNull
    @Column(nullable = false)
    @ElementCollection
    private List<String> roles = new ArrayList<>();

    public User() {}

    public User(String provider, String providerId, String name, String email, Instant dateJoined, List<String> roles) {
        this.dateJoined = dateJoined;
        this.userId = new UserId(provider, providerId);
        this.email = email;
        this.name = name;
        this.roles = roles;
    }

    public Instant getDateJoined() {
        return dateJoined;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setDateJoined(Instant dateJoined) {
        this.dateJoined = dateJoined;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User: {id = " + userId +
                ", name = '" + name + "'" +
                ", email = '" + email + "'" +
                ", date joined = " + dateJoined +
                ", roles: [" + String.join(",", roles) + "]}";
    }
}
