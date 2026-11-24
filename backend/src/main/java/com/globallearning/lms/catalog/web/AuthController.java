package com.globallearning.lms.catalog.web;

import com.globallearning.lms.catalog.service.AuthenticationService;
import com.globallearning.lms.catalog.web.dto.LoginRequest;
import com.globallearning.lms.catalog.web.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authenticationService.login(request.getUsername(), request.getPassword());
    }
}
