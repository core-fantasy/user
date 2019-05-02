package com.corefantasy.user.controller

import groovy.transform.ToString;

@ToString(includeNames = true)
class RegisteredUser {
    String provider
    String providerId
    List<String> roles = []
}
