package com.corefantasy.user.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.corefantasy.user.model.User;
import io.micronaut.context.annotation.Property;

import javax.inject.Singleton;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

@Singleton
public class JwtProvider {

    static final String issuer = "core-fantasy";
    static final String userIdClaim = "user-id";

    private Algorithm algorithm;
    private JWTVerifier verifier;

    // TODO: secret from env + k8s secret (see application.yaml)
    public JwtProvider(@Property(name="core-fantasy.user.jwt-secret") String secret) {
        algorithm = Algorithm.HMAC256(secret);
        verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
    }

    public String getToken(User user) {
        return getToken(user, 14, ChronoUnit.DAYS); // TODO: configurable default
    }

    public String getToken(User user, long duration, TemporalUnit durationUnit) {
        var now = ZonedDateTime.now(ZoneId.of("UTC"));
        var expiration = now.plus(duration, durationUnit);
        String token = JWT.create()
                .withExpiresAt(Date.from(expiration.toInstant()))
                .withIssuedAt(Date.from(now.toInstant()))
                .withIssuer(issuer)
                .withClaim(userIdClaim, user.getId())
                .sign(algorithm);
        return token;
    }

    public DecodedJWT getDecodedToken(String token) throws JWTVerificationException {
        return verifier.verify(token);
    }
}
