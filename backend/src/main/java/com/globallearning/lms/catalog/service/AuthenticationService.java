package com.globallearning.lms.catalog.service;

import com.globallearning.lms.catalog.web.dto.LoginResponse;
import java.time.Duration;
import java.time.Instant;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private static final Duration TOKEN_LIFETIME = Duration.ofMinutes(15);

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    public LoginResponse login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(username, password));

        Instant issuedAt = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("lms-catalog")
            .issuedAt(issuedAt)
            .expiresAt(issuedAt.plus(TOKEN_LIFETIME))
            .subject(authentication.getName())
            .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
            JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();

        return new LoginResponse(token, "Bearer", TOKEN_LIFETIME.toSeconds());
    }
}
