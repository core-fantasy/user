package com.corefantasy.user.controller;

import com.corefantasy.user.dao.exception.RegisterUserException;
import com.corefantasy.user.jwt.JwtProvider;
import com.corefantasy.user.loginprovider.LoginProvider;
import com.corefantasy.user.loginprovider.LoginProviderException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.validation.Validated;
import com.corefantasy.user.dao.UserRepository;
import com.corefantasy.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Validated
@Controller("/v1")
public class UserController {

    private static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    // TODO: swagger

    // TODO: embed com.corefantasy.user ID into JWT, use that ID to do com.corefantasy.user operations, only allow for that ID

    final private JwtProvider jwtProvider;
    private final Map<String, LoginProvider> providerMap = new HashMap<>();
    final private UserRepository userRepository;

    public UserController(UserRepository userRepository, JwtProvider jwtProvider, List<LoginProvider> providerList) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        /*LOGGER.info("Adding login provider: {}", providerList.getName());
        providerMap.put(providerList.getName(), providerList);*/

        providerList.forEach(loginProvider -> {
            LOGGER.info("Adding login provider: {}", loginProvider.getName());
            providerMap.put(loginProvider.getName(), loginProvider);
        });

    }

    @Get(uri = "/com/corefantasy/user/{id}")
    public User getUser(String id) {
        return null;
    }

    @Put(uri = "/com/corefantasy/user")
    public User updateUser(@Body User user) { // TODO: change param to request object (something without ID)
        return user;
    }

    @Delete(uri = "/com/corefantasy/user")
    public void deleteUser() {
    }

    @Post(uri = "/login/{name}")
    public HttpResponse<String> login(String name, @Body Map<String, Object> credentials) {

        LOGGER.info("Logging in via '{}', with credentials:\n{}", name, credentials);

        HttpResponse<String> response;
        try {
            var provider = providerMap.get(name);
            if (provider != null) {
                User user = providerMap.get(name).login(credentials);
                user = userRepository.registerUser(user);
                String token = jwtProvider.getToken(user);
                // TODO: token as a cookie?
                response = HttpResponse.ok(token);
            }
            else {
                LOGGER.warn("Invalid login provider given, '{}'.", name);
                response = HttpResponse.badRequest(
                        "Attempted to register with unsupported provider, \"" + name + "\".");
            }
        }
        catch (UnauthorizedException ue) {
            LOGGER.warn("Unathorized access to '{}' with credentials '{}'.", name, credentials);
            response = HttpResponse.unauthorized();
        }
        catch (LoginProviderException lpe) {
            LOGGER.warn("Error validating user through '{}'.", name, lpe);
            response = HttpResponse.serverError(lpe.getMessage());
        }
        catch (RegisterUserException rue) {
            LOGGER.error("Error registering user through '{}'.", name, rue);
            response = HttpResponse.serverError(rue.getMessage());
        }
        catch (Exception e) {
            LOGGER.error("Unhandled exception during registration through '{}'.", name, e);
            response = HttpResponse.serverError("Unhandled error during registration.");
        }
        return response;
    }

    @Get(uri = "/validate")
    public void validate() {
        // TODO: validate user ID in DB?
    }

    /**
     * Just in case error handler.
     * @param request HTTP request data
     * @param exception exception thrown
     * @return response
     */
    @Error
    public HttpResponse<String> jsonError(HttpRequest request, Exception exception) {
        LOGGER.error("Unhandled error while processing {}.", request.getPath(), exception);
        return HttpResponse.<String>status(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error.")
                .body("Unexpected error during processing.");
    }
}
