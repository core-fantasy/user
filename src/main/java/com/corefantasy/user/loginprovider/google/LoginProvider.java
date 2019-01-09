package com.corefantasy.user.loginprovider.google;

import com.corefantasy.user.controller.UnauthorizedException;
import com.corefantasy.user.loginprovider.LoginProviderException;
import com.corefantasy.user.model.User;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.micronaut.context.annotation.Value;

import javax.inject.Singleton;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Singleton
public class LoginProvider implements com.corefantasy.user.loginprovider.LoginProvider {

    @Value("") // TODO: google client app, get this from env + k8s secret
    final private String CLIENT_ID = "";

    @Override
    public String getName() {
        return "Google";
    }

    @Override
    public User login(Object credentials) throws LoginProviderException {
        try {
            NetHttpTransport transport = new NetHttpTransport.Builder()
                    .build();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify((String) credentials);
            if (idToken != null) {
                Payload payload = idToken.getPayload();

                String userId = "google/" + payload.getSubject();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                return new User(userId, name, email);
            }
            throw new UnauthorizedException("Invalid Google login ID token was provided.");
        }
        catch (GeneralSecurityException | IOException e) {
            throw new LoginProviderException("Error verifying Google ID \"" + credentials + "\".", e);
        }
    }
}
