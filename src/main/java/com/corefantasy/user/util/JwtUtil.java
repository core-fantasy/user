package com.corefantasy.user.util;

import com.nimbusds.jwt.JWTParser;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.token.jwt.validator.JwtTokenValidator;

import javax.inject.Singleton;
import java.text.ParseException;

@Singleton
public class JwtUtil {

    final private JwtTokenValidator tokenValidator;
    final private String cookieName;

    public JwtUtil(JwtTokenValidator tokenValidator, @Value("${micronaut.security.token.jwt.cookie.cookie-name}") String cookieName) {
        this.tokenValidator = tokenValidator;
        this.cookieName = cookieName;
    }

    public String extractJwt(HttpRequest<?> request) {
        var cookie = request.getCookies().findCookie(cookieName)
                .orElseThrow(()->{throw new IllegalArgumentException("No '" + cookieName + "' present in HTTP request.");});
        return cookie.getValue();
    }

    public String getProvider(HttpRequest<?> request) throws ParseException {
        // name set in authorization service
        return getClaim(extractJwt(request), "core-fantasy.com/provider");
    }

    public String getProviderId(HttpRequest<?> request) throws ParseException {
        // name set in authorization service
        return getClaim(extractJwt(request), "core-fantasy.com/provider-id");
    }

    public String getClaim(String jwtString, String claim) throws ParseException {
        return JWTParser.parse(jwtString).getJWTClaimsSet().getStringClaim(claim);
    }
}
