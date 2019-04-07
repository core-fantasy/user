package com.corefantasy.user.controller;

import com.corefantasy.user.controller.commands.RegisteredUser;
import com.corefantasy.user.controller.commands.RegisterUser;
import com.corefantasy.user.dao.exception.RegisterUserException;
import com.corefantasy.user.dao.exception.UserAlreadyRegisteredException;
import com.corefantasy.user.model.PublicUser;
import com.corefantasy.user.util.JwtUtil;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import com.corefantasy.user.dao.UserRepository;
import com.corefantasy.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

@Validated
@Controller("/v1")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    final private JwtUtil jwtUtil;
    final private UserRepository userRepository;

    public UserController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    /**
     * Returns information about the current user.
     * @param request request with JWT token cookie included
     * @return current user's information
     */
    @Get(uri = "/me")
    @Secured({"ROLE_USER"})
    public PublicUser getMyDetails(HttpRequest<?> request) {
        String userId = jwtUtil.getUserId(request);
        return userRepository.getUserById(userId).orElseThrow(()->{
            LOGGER.error("Unexpectedly could not find user data for self. Id: {}", userId);
            throw new RuntimeException("Unexpectedly could not find self.");
        });
    }

    /**
     * Dummy endpoint to test access. Can be deleted.
     * @return
     */
    @Get("/test")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public String test() {
        LOGGER.error("logging some data");
        return "test endpoint\n";
    }

    @Get(uri = "/user/{id}")
    public PublicUser getUser(String id) {
        return null;
    }

    @Put(uri = "/user")
    public PublicUser updateUser(@Body User user) { // TODO: change param to request object (something without ID)
        return null;
    }

    @Delete(uri = "/user")
    public void deleteUser() {
        // TODO: only let normal users delete themselves
        // TODO: need to clean up other data (leagues, etc.)
    }

    @Delete(uri = "/user/{id}")
    @Secured({"ROLE_ADMIN"})
    public void deleteUserAdmin(String id) {
    }

    @Post(uri = "/register")
    @Secured({"INTERNAL_USER"})
    public HttpResponse<?> registerUser(@Body RegisterUser registerUser) {
        HttpResponse<?> response;
        try {
            LOGGER.info("Registering User: {}.", registerUser);
            User user = userRepository.registerUser(registerUser);
            RegisteredUser registeredUser = new RegisteredUser();
            registeredUser.setId(user.getId());
            registeredUser.setRoles(List.copyOf(user.getRoles()));
            response = HttpResponse.created(registeredUser, new URI("/v1/user/" + registeredUser.getId()));
        }
        catch (UserAlreadyRegisteredException uare) {
            LOGGER.warn(uare.getMessage());
            response = HttpResponse.status(HttpStatus.CONFLICT, "User " + registerUser.getId() + " is already registered.");
        }
        catch (RegisterUserException rue) {
            LOGGER.error("Error registering user '{}'.", registerUser, rue);
            response = HttpResponse.serverError("Internal error registering user.");
        }
        catch (Exception e) {
            LOGGER.error("Unhandled exception registering '{}'.", registerUser, e);
            response = HttpResponse.serverError("Unhandled error during registration.");
        }

        return response;
    }

    /**
     * Just in case error handler.
     * @param request HTTP request data
     * @param exception exception thrown
     * @return response
     */
    @Error
    public HttpResponse<String> exceptionHandler(HttpRequest request, Exception exception) {
        LOGGER.error("Unhandled error while processing {}.", request.getPath(), exception);
        return HttpResponse.<String>status(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error.")
                .body("Unexpected error during processing.");
    }
}
