package com.corefantasy.user.dao;

import com.corefantasy.user.dao.exception.RegisterUserException;
import io.micronaut.configuration.hibernate.jpa.scope.CurrentSession;
import io.micronaut.spring.tx.annotation.Transactional;
import com.corefantasy.user.model.User;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Singleton
public class UserRepositoryImpl implements UserRepository{

    @PersistenceContext
    final private EntityManager entityManager;

    public UserRepositoryImpl(@CurrentSession EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public User registerUser(User user) throws RegisterUserException {
        try {
            user = entityManager.merge(user);
            return user;
        }
        catch (Exception e) {
            throw new RegisterUserException("Internal error registering user.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(@NotNull String id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    @Override
    @Transactional
    public void deleteUser(@NotNull String id) {
        getUserById(id).ifPresent(entityManager::remove);
    }

    @Override
    public void updateUser(@NotNull User newUserData) {
        entityManager.merge(newUserData);
    }
}
