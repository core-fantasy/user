package com.corefantasy.user.dao;

import com.corefantasy.user.dao.exception.UserAlreadyRegisteredException;
import com.corefantasy.user.model.PublicUser;
import com.corefantasy.user.controller.RegisterUser;
import com.corefantasy.user.dao.exception.RegisterUserException;
import com.corefantasy.user.model.UserId;
import io.micronaut.configuration.hibernate.jpa.scope.CurrentSession;
import io.micronaut.spring.tx.annotation.Transactional;
import com.corefantasy.user.model.User;
import org.hibernate.exception.ConstraintViolationException;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Singleton
public class UserRepositoryImpl implements UserRepository {

    @PersistenceContext
    final private EntityManager entityManager;

    public UserRepositoryImpl(@CurrentSession EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public User registerUser(RegisterUser registerUser) throws RegisterUserException {
        try {
            User user = new User(registerUser.getProvider(), registerUser.getProviderId(), registerUser.getName(), registerUser.getEmail(),
                    Instant.now(), Collections.singletonList("ROLE_USER"));
            entityManager.persist(user);
            entityManager.flush(); // Flush here, otherwise flush (and primary key violation) happens in caller.
            return user;
        }
        catch (Exception e) {
            // See https://stackoverflow.com/questions/3502279/how-to-handle-jpa-unique-constraint-violations
            boolean primaryKeyConstraint = false;
            Throwable copy = e;
            do {
                if (copy instanceof ConstraintViolationException) {
                    primaryKeyConstraint = true;
                    break;
                }
                copy = copy.getCause();
            } while (copy != null);

            if (primaryKeyConstraint) {
                throw new UserAlreadyRegisteredException("User " + registerUser.getProvider() + "/"
                        + registerUser.getProviderId() + " is already registered.");
            }
            throw new RegisterUserException("Internal error registering user.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PublicUser> getUserById(@NotNull String provider, @NotNull String providerId) {
        UserId userId = new UserId(provider, providerId);
        User user = entityManager.find(User.class, userId);
        if (user != null) {
            return Optional.of(new PublicUser(user));
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<String>> getUserRoles(@NotNull String provider, @NotNull String providerId) {
        UserId userId = new UserId(provider, providerId);
        User user = entityManager.find(User.class, userId);
        if (user != null) {
            return Optional.of(List.copyOf(user.getRoles()));
        }
        return Optional.empty();
    }


    @Override
    @Transactional
    public void deleteUser(@NotNull String id) {
        //getUserById(id).ifPresent(entityManager::remove);
    }

    @Override
    public void updateUser(@NotNull User newUserData) {
        //entityManager.merge(newUserData);
    }
}
