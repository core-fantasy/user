package com.corefantasy.user.controller

import groovy.transform.ToString

@ToString(includeNames = true)
class RegisterUser {
    String provider
    String providerId
    String name
    String email

    RegisterUser() {}

    RegisterUser(String provider, String providerId, String name, String email) {
        this.provider = provider
        this.providerId = providerId
        this.name = name
        this.email = email
    }
}
