package com.corefantasy.user.jwt;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.corefantasy.user.model.User;
import io.micronaut.context.annotation.Property;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
@Property(name = "core-fantasy.user.jwt-secret", value = "DummySecret")
public class JwtProviderTest {
    @Inject
    JwtProvider jwtProvider;

    private final User user = new User("ID1", "Name1", "email1@email.com");

    @Test
    void token() throws InterruptedException {
        String token = jwtProvider.getToken(user);
        assertNotNull(token);

        Thread.sleep(50); // To ensure no collisions with Date checks below.

        DecodedJWT jwt = jwtProvider.getDecodedToken(token);
        assertEquals(JwtProvider.issuer, jwt.getIssuer());
        assertEquals(user.getId(), jwt.getClaim(JwtProvider.userIdClaim).asString());
        assertTrue(jwt.getExpiresAt().after(new Date()));
        assertTrue(jwt.getIssuedAt().before(new Date()));

        assertThrows(JWTDecodeException.class, ()->jwtProvider.getDecodedToken("abc"));
    }
}
