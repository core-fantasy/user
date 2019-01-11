package com.corefantasy.user.controller;

import com.corefantasy.user.jwt.JwtProvider;
import com.corefantasy.user.loginprovider.LoginProvider;
import com.corefantasy.user.loginprovider.LoginProviderException;
import com.corefantasy.user.loginprovider.google.GoogleLoginProvider;
import com.corefantasy.user.model.User;
import io.micronaut.context.annotation.Property;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MicronautTest;
import io.micronaut.test.annotation.MockBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@MicronautTest
@Property(name = "core-fantasy.user.jwt-secret", value = "DummySecret")
class UserControllerTest {

    @Inject
    JwtProvider jwtProvider;

    @Inject
    LoginProvider provider;

    @Inject
    @Client("/")
    RxHttpClient httpClient;

    private static final String LOGIN_PROVIDER = "MockLoginProvider";

    private static final Map<String, Object> CREDENTIALS;
    private static final User GOOD_USER = new User("GoodUser", "GoodUser", "GoodUser@email.com");

    static {
        CREDENTIALS = new HashMap<>();
        CREDENTIALS.put("token", "abc");
    }

    @BeforeEach
    void setup() {
        when(provider.getName()).thenReturn(LOGIN_PROVIDER);
    }

    @Test
    @SuppressWarnings("unchecked")
    void loginInvalidProvider() {
        var request = HttpRequest.POST("/v1/login/junk", CREDENTIALS);
        HttpResponse<String> response = httpClient
                .exchange(request, Argument.of(String.class), Argument.of(String.class))
                .onErrorReturn((throwable) -> (HttpResponse<String>)((HttpClientResponseException) throwable).getResponse())
                .blockingFirst();
        assertEquals(400, response.code());
        assertTrue(response.body().contains("unsupported provider"));
    }

    @Test
    void login() {
        when(provider.login(CREDENTIALS)).thenReturn(GOOD_USER);
        final HttpResponse<String> response = httpClient.toBlocking().exchange(
                HttpRequest.POST("/v1/login/" + LOGIN_PROVIDER, CREDENTIALS), String.class);
        assertEquals(200, response.code());
        assertDoesNotThrow(() -> jwtProvider.getDecodedToken(response.body()));
    }

    @Test
    @SuppressWarnings("unchecked")
    void loginUnauthorized() {
        when(provider.login(CREDENTIALS)).thenThrow(new UnauthorizedException("User isn't allowed."));

        var request = HttpRequest.POST("/v1/login/" + LOGIN_PROVIDER, CREDENTIALS);
        final HttpResponse<?> response = httpClient
                .exchange(request, Argument.of(String.class))
                .onErrorReturn((throwable) -> (HttpResponse<String>)((HttpClientResponseException) throwable).getResponse())
                .blockingFirst();

        assertEquals(401, response.code());
    }

    @Test
    @SuppressWarnings("unchecked")
    void loginProviderError() {
        when(provider.login(CREDENTIALS)).thenThrow(new LoginProviderException("User isn't allowed."));

        var request = HttpRequest.POST("/v1/login/" + LOGIN_PROVIDER, CREDENTIALS);
        final HttpResponse<String> response = httpClient
                .exchange(request, Argument.of(String.class), Argument.of(String.class))
                .onErrorReturn((throwable) -> (HttpResponse<String>)((HttpClientResponseException) throwable).getResponse())
                .blockingFirst();

        assertEquals(500, response.code());
        assertTrue(response.body().contains("User isn't allowed."), response.body());
    }

    @Test
    @SuppressWarnings("unchecked")
    void loginRegisterError() {
        when(provider.login(CREDENTIALS)).thenReturn(null);

        var request = HttpRequest.POST("/v1/login/" + LOGIN_PROVIDER, CREDENTIALS);
        final HttpResponse<String> response = httpClient
                .exchange(request, Argument.of(String.class), Argument.of(String.class))
                .onErrorReturn((throwable) -> (HttpResponse<String>)((HttpClientResponseException) throwable).getResponse())
                .blockingFirst();

        assertEquals(500, response.code());
        assertTrue(response.body().contains("Internal error registering user"), response.body());
    }

    @Test
    @SuppressWarnings("unchecked")
    void loginUnexpectedError() {
        when(provider.login(CREDENTIALS)).thenThrow(new ArrayIndexOutOfBoundsException());

        var request = HttpRequest.POST("/v1/login/" + LOGIN_PROVIDER, CREDENTIALS);
        final HttpResponse<String> response = httpClient
                .exchange(request, Argument.of(String.class), Argument.of(String.class))
                .onErrorReturn((throwable) -> (HttpResponse<String>)((HttpClientResponseException) throwable).getResponse())
                .blockingFirst();

        assertEquals(500, response.code());
        assertTrue(response.body().contains("Unhandled error during registration."), response.body());
    }

    @MockBean(GoogleLoginProvider.class)
    @SuppressWarnings("unused")
    LoginProvider getDummyLoginProvider() {
        return mock(LoginProvider.class);
    }
}
