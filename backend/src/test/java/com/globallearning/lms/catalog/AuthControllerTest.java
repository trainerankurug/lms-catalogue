package com.globallearning.lms.catalog;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.globallearning.lms.catalog.service.AuthenticationService;
import com.globallearning.lms.catalog.web.AuthController;
import com.globallearning.lms.catalog.web.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    value = AuthController.class,
    properties = {
        "lms.auth.username=test-user",
        "lms.auth.password=test-password",
        "lms.auth.jwt-secret=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY="
    }
)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    @Test
    void returnsBearerTokenForValidLoginRequest() throws Exception {
        given(authenticationService.login("lms-user", "secret-password"))
            .willReturn(new LoginResponse("signed-token", "Bearer", 900));

        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"lms-user\",\"password\":\"secret-password\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("signed-token"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").value(900));
    }

    @Test
    void returnsUnauthorizedForInvalidCredentials() throws Exception {
        given(authenticationService.login("lms-user", "wrong-password"))
            .willThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content("{\"username\":\"lms-user\",\"password\":\"wrong-password\"}"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("INVALID_CREDENTIALS"));
    }
}
