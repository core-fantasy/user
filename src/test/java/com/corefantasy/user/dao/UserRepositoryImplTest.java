package com.corefantasy.user.dao;

import com.corefantasy.user.DbProperties;
import com.corefantasy.user.dao.exception.RegisterUserException;
import com.corefantasy.user.model.User;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.Optional;

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
    void registerUser() {
        // First time registration
        User user1 = new User("abc123", "Test User", "user@user.com");
        User registeredUser = repository.registerUser(user1);
        compareUsers(user1, registeredUser);

        // Re-register user
        User sameUser = new User(user1.getId(), user1.getName(), user1.getEmail());
        registeredUser = repository.registerUser(sameUser);
        compareUsers(user1, registeredUser);

        // Register user with different name
        User changedUser = new User(user1.getId(), "New Name", "new@email.com");
        registeredUser = repository.registerUser(changedUser);
        compareUsers(changedUser, registeredUser);

        // Null user
        assertThrows(RegisterUserException.class, ()-> repository.registerUser(null));

        // Null fields
        assertThrows(RegisterUserException.class, ()-> repository.registerUser(new User(null, null, null)));
    }

    @Test
    void getUserById() {
        User user1 = new User("abc123", "Test User", "user@user.com");
        User registeredUser = repository.registerUser(user1);

        Optional<User> user = repository.getUserById(registeredUser.getId());
        assertTrue(user.isPresent());
        compareUsers(registeredUser, user.get());

        // Get non-existent user
        final Optional<User> finalUser = repository.getUserById("Not an id.");
        assertTrue(finalUser.isEmpty(), () -> "User id: " + finalUser.get().getId());
    }

    private void compareUsers(User expected, User actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
    }
}
