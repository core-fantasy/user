package com.corefantasy.user.controller;

import com.corefantasy.user.dao.UserRepository;
import com.corefantasy.user.model.PublicUser;
import com.corefantasy.user.util.JwtUtil;
import io.micronaut.http.HttpResponse;
import io.micronaut.security.authentication.UserDetails;
import io.micronaut.security.token.generator.TokenGenerator;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.Collections;

import static org.mockito.Mockito.*;

@MicronautTest
class UserControllerTest {

    @Inject
    TokenGenerator tokenGenerator;

    @Inject
    JwtUtil jwtUtil;

    UserRepository userRepository;

    UserController userController;

    private String defaultUserToken;
    private UserDetails userDetails = new UserDetails("my-user-name", Collections.singletonList("ROLE_USER"));

    // TODO: fix this test

    @BeforeEach
    void setup() {
        defaultUserToken = tokenGenerator.generateToken(userDetails, 5 * 1000).get();
        userRepository = mock(UserRepository.class);
        userController = new UserController(jwtUtil, userRepository);
    }

    @Test
    void getMyDetails() {
        /*
        when(userRepository.getUserById(userDetails.getUsername())).then(invocation ->
                new PublicUser(userDetails.getUsername(), "jim", "you@me.com"));

        PublicUser user = client.retrieve(
                HttpRequest.GET("/me").cookie(Cookie.of("JWT", defaultUserToken)),
                PublicUser.class
        ).blockingFirst();
        */

        // verify(userRepository).getUserById(userDetails.getUsername());
    }

    @Test
    void registerUser() {
        /*
        RegisterUser registerUser = new RegisterUser("id123", "name321", "email@email.email");

        when(userRepository.registerUser(registerUser))
                .then(invocation ->
                        HttpResponse.created(null));
*/
       // userRepository.registerUser(registerUser);

        //verify(userRepository).registerUser(registerUser);
    }

    /*
    I would love to use this, but it always causes a NonUniqueBeanException
    between the mock and the actual bean
    @MockBean(UserRepositoryImpl.class)
    @SuppressWarnings("unused")
    UserRepository userRepositoryMock() {
        return mock(UserRepository.class);
    }
    */
}
