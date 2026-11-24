package com.globallearning.lms.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.globallearning.lms.catalog.service.AuthenticationService;
import com.globallearning.lms.catalog.web.dto.LoginResponse;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void authenticatesCredentialsAndReturnsShortLivedBearerToken() {
        Instant issuedAt = Instant.now();
        given(authenticationManager.authenticate(any(Authentication.class)))
            .willReturn(UsernamePasswordAuthenticationToken.authenticated("lms-user", null, List.of()));
        given(jwtEncoder.encode(any(JwtEncoderParameters.class)))
            .willReturn(Jwt.withTokenValue("signed-token")
                .header("alg", "HS256")
                .subject("lms-user")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(900))
                .build());

        LoginResponse response = authenticationService.login("lms-user", "secret-password");

        assertThat(response.accessToken()).isEqualTo("signed-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(900);

        ArgumentCaptor<Authentication> authenticationCaptor = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(authenticationCaptor.capture());
        assertThat(authenticationCaptor.getValue().getPrincipal()).isEqualTo("lms-user");
        assertThat(authenticationCaptor.getValue().getCredentials()).isEqualTo("secret-password");
    }
}
