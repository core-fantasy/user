package com.corefantasy.user.dao;

import com.corefantasy.user.model.PublicUser;
import com.corefantasy.user.controller.commands.RegisterUser;
import com.corefantasy.user.dao.exception.RegisterUserException;
import com.corefantasy.user.model.User;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface UserRepository {
    User registerUser(@NotNull RegisterUser user) throws RegisterUserException;

    Optional<PublicUser> getUserById(@NotNull String id);

    void deleteUser(@NotNull String id);

    void updateUser(@NotNull User newUserData);
}
