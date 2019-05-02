package com.corefantasy.user.model

import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
@ToString(includeNames = true)
class UserId implements Serializable{
    @Column
    String provider = ""

    @Column
    String providerId = ""

    UserId() {
    }

    UserId(String provider, String providerId) {
        this.provider = provider
        this.providerId = providerId
    }

    @Override
    boolean equals(Object o) {
        if (this == o) {
            return true
        }
        if (!(o instanceof UserId)) {
            return false
        }
        UserId that = (UserId) o
        return Objects.equals(getProvider(), that.getProvider()) &&
                Objects.equals(getProviderId(), that.getProvider())
    }

    @Override
    int hashCode() {
        return Objects.hash(getProvider(), getProviderId())
    }
}