package com.corefantasy.user.controller;

import com.corefantasy.user.dao.exception.RegisterUserException;
import com.corefantasy.user.dao.exception.UserAlreadyRegisteredException;
import com.corefantasy.user.model.PublicUser;
import com.corefantasy.user.util.JwtUtil;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.simple.SimpleHttpResponseFactory;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import com.corefantasy.user.dao.UserRepository;
import com.corefantasy.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.text.ParseException;
import java.util.List;

@Validated
@Controller("/v1")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    final private JwtUtil jwtUtil;
    final private SimpleHttpResponseFactory responseFactory;
    final private UserRepository userRepository;

    public UserController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.responseFactory = new SimpleHttpResponseFactory();
        this.userRepository = userRepository;
    }

    /**
     * Returns information about the current user.
     * @param request request with JWT token cookie included
     * @return current user's information
     */
    @Get(uri = "/me")
    @Secured({"ROLE_USER"})
    public PublicUser getMyDetails(HttpRequest<?> request) throws ParseException {
        String provider = jwtUtil.getProvider(request);
        String providerId = "";
        return userRepository.getUserById(provider, providerId).orElseThrow(() -> {
            LOGGER.error("Unexpectedly could not find user data for self. Provider: {}, provider Id: {}",
                    provider, providerId);
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

    @Get(uri = "/user/{provider}/{providerId}")
    public PublicUser getUser(@PathVariable String provider, @PathVariable String providerId) {
        return null;
    }

    @Put(uri = "/user/{provider}/{providerId}")
    public PublicUser updateUser(@PathVariable String provider, @PathVariable String providerId, @Body Object userData) {
        // TODO: figure out body
        return null;
    }

    @Delete(uri = "/user")
    public void deleteUser() {
        // TODO: only let normal users delete themselves
        // TODO: need to clean up other data (leagues, etc.)
    }

    @Delete(uri = "/user/{provider}/{providerId}")
    @Secured({"ROLE_ADMIN"})
    public void deleteUserAdmin(@PathVariable String provider, @PathVariable String providerId) {
    }

    @Post(uri = "/register")
    @Secured({"INTERNAL_USER"})
    public HttpResponse<?> registerUser(@Body RegisterUser registerUser) {
        MutableHttpResponse<?> response;
        try {
            var roles = userRepository.getUserRoles(registerUser.getProvider(), registerUser.getProviderId());

            RegisteredUser registeredUser = new RegisteredUser();

            if (roles.isPresent()) {
                LOGGER.info("User {} already registered. Returning roles: {}", registerUser, roles.get());
                registeredUser.setProvider(registerUser.getProvider());
                registeredUser.setProviderId(registerUser.getProviderId());
                registeredUser.setRoles(List.copyOf(roles.get()));
                response = HttpResponse.ok().body(registeredUser);
            }
            else {
                LOGGER.info("Registering User: {}.", registerUser);
                User user = userRepository.registerUser(registerUser);
                registeredUser.setProvider(user.getUserId().getProvider());
                registeredUser.setProviderId(user.getUserId().getProviderId());
                registeredUser.setRoles(List.copyOf(user.getRoles()));
                response = HttpResponse.created(registeredUser);
            }
            var location = new URI("/v1/user/" + registeredUser.getProvider() + "/" + registeredUser.getProviderId());
            response.header("Location", location.getPath());
        }
        catch (UserAlreadyRegisteredException uare) {
            LOGGER.warn(uare.getMessage());
            response = HttpResponse.status(HttpStatus.CONFLICT, "User " + registerUser + " is already registered.");
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
     * Error handler for bad JWT. Probably shouldn't ever get called since all controller methods will theoretically
     * have already checked the JWT.
     * @param request HTTP request data
     * @param exception exception thrown
     * @return response
     */
    @Error
    public HttpResponse<String> exceptionHandler(HttpRequest request, ParseException exception) {
        LOGGER.error("Invalid JWT provided for request to {}.", request.getPath(), exception);
        return HttpResponse.<String>status(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error with JWT.")
                .body("Unexpected error handling JWT.");
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
