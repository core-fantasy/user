package com.corefantasy.user.dao;

import com.corefantasy.user.controller.commands.RegisterUser;
import com.corefantasy.user.dao.exception.RegisterUserException;
import com.corefantasy.user.dao.exception.UserAlreadyRegisteredException;
import com.corefantasy.user.model.PublicUser;
import com.corefantasy.user.model.User;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class UserRepositoryImplTest {

    @Inject
    private EntityManager entityManager;

    private UserRepository repository;

    @BeforeEach
    void setup() {
        repository = new UserRepositoryImpl(entityManager);
    }

    @Test
    void registerUser() {
        // First time registration
        RegisterUser registerUser = new RegisterUser("abc123", "Test User", "user@user.com");
        User registeredUser = repository.registerUser(registerUser);
        assertEquals(registerUser.getId(), registeredUser.getId());
        assertEquals(registerUser.getName(), registeredUser.getName());
        assertEquals(registerUser.getEmail(), registeredUser.getEmail());
        assertIterableEquals(Collections.singletonList("ROLE_USER"), registeredUser.getRoles());

        // Close the session so the next test works correctly.
        entityManager.clear();

        // Register user with different name/email
        RegisterUser changedUser = new RegisterUser(registerUser.getId(), "New Name", "new@email.com");
        assertThrows(UserAlreadyRegisteredException.class, () -> repository.registerUser(changedUser));

        // Null user
        assertThrows(RegisterUserException.class, ()-> repository.registerUser(null));

        // Null fields
        assertThrows(RegisterUserException.class, ()-> repository.registerUser(new RegisterUser(null, null, null)));
    }

    @Test
    void getUserById() {
        RegisterUser registerUser = new RegisterUser("abc123", "Test User", "user@user.com");
        User registeredUser = repository.registerUser(registerUser);

        Optional<PublicUser> userOpt = repository.getUserById(registerUser.getId());
        assertTrue(userOpt.isPresent());
        PublicUser user = userOpt.get();
        assertEquals(registeredUser.getId(), user.getId());
        assertEquals(registeredUser.getName(), user.getName());
        assertEquals(registeredUser.getEmail(), user.getEmail());

        // Get non-existent user
        final Optional<PublicUser> finalUser = repository.getUserById("Not an id.");
        assertTrue(! finalUser.isPresent(), () -> "User id: " + finalUser.get().getId());
    }
}
