package com.corefantasy.user.model

import groovy.transform.ToString

/**
 * User exposed to the outside world. Part of the reason for this class is that using
 * {@link com.corefantasy.user.model.User} outside of a JPA transaction/session causes an exception to be thrown.
 */
@ToString(includeNames = true)
class PublicUser {
    final String provider
    final String providerId
    final String name
    final String email

    PublicUser(String provider, String providerId, String name, String email) {
        this.provider = provider
        this.providerId = providerId
        this.name = name
        this.email = email
    }

    public PublicUser(User user) {
        this(user.getUserId().getProvider(), user.getUserId().getProviderId(), user.getName(), user.getEmail());
    }
}
