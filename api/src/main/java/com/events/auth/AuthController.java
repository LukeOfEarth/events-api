package com.events.auth;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;


@RestController
public class AuthController {
    final String CLIENT_ID = "355243827358-rij4lulni9sho1bivc2po74gtqs7uhlp.apps.googleusercontent.com";

    @GetMapping("auth/login")
    public String authLogin(@AuthenticationPrincipal OidcUser principle) throws GeneralSecurityException, IOException {

        OidcIdToken rawIdToken = principle.getIdToken();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();


        GoogleIdToken idToken = verifier.verify(rawIdToken.getTokenValue());
        if (idToken != null) {
            Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");
        }
        else {
            return "Login Unsuccessful";
        }
        return "Login Successful";

    }
    @GetMapping("/")
    public String login() {
        return "Hello World";
    }

}