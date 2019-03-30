package com.corefantasy.user.model;

/**
 * User exposed to the outside world. Part of the reason for this class is that using
 * {@link com.corefantasy.user.model.User} outside of a JPA transaction/session causes an exception to be thrown.
 */
public class PublicUser {
    private final String id;
    private final String name;
    private final String email;

    public PublicUser(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public PublicUser(User user) {
        this(user.getId(), user.getName(), user.getEmail());
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
