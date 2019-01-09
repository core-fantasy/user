package com.corefantasy.user.dao;

import com.corefantasy.user.DbProperties;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@DbProperties // TODO: needed?
class UserRepositoryImplTest {

    @Inject
    EntityManager entityManager;

    UserRepository repository;

    @BeforeEach
    void setup() {
        repository = new UserRepositoryImpl(entityManager);
    }

    @Test
    void test() {
    }
}
