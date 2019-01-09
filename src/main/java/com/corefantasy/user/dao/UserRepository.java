package com.corefantasy.user.dao;

import com.corefantasy.user.dao.exception.RegisterUserException;
import com.corefantasy.user.model.User;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface UserRepository {
    User registerUser(@NotNull User user) throws RegisterUserException;

    Optional<User> getUserById(@NotNull String id);

    void deleteUser(@NotNull String id);

    void updateUser(@NotNull User newUserData);
}
