package com.corefantasy.user.dao;

import com.corefantasy.user.model.PublicUser;
import com.corefantasy.user.controller.RegisterUser;
import com.corefantasy.user.dao.exception.RegisterUserException;
import com.corefantasy.user.model.User;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User registerUser(@NotNull RegisterUser user) throws RegisterUserException;

    Optional<PublicUser> getUserById(@NotNull String provider, @NotNull String providerId);

    Optional<List<String>> getUserRoles(@NotNull String provider, @NotNull String providerId);

    void deleteUser(@NotNull String id);

    void updateUser(@NotNull User newUserData);
}
