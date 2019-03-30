package com.corefantasy.user.util;

import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.token.jwt.validator.JwtTokenValidator;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import javax.inject.Singleton;

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

    public String getUserId(HttpRequest<?> request) {
        return getUserId(extractJwt(request));
    }

    public String getUserId(String jwt) {
        Publisher<Authentication> auth = tokenValidator.validateToken(jwt);
        var sub = new Subscriber<Authentication>() {
            Authentication auth;
            @Override
            public void onSubscribe(Subscription s) {
                s.request(1);
            }

            @Override
            public void onNext(Authentication authentication) {
                auth = authentication;
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onComplete() {
            }
        };
        auth.subscribe(sub);
        return sub.auth.getName();
    }
}
