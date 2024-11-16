package com.example.servicehub.controller;

import com.example.servicehub.model.dto.LoginResponseDto;
import com.example.servicehub.model.dto.LoginRequestDto;
import com.example.servicehub.model.dto.RegisterRequestDto;
import com.example.servicehub.model.dto.RegisterResponseDto;
import com.example.servicehub.service.AuthenticationService;
import com.example.servicehub.service.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final LogoutService logoutService;

    public AuthenticationController(AuthenticationService authenticationService, LogoutService logoutService) {
        this.authenticationService = authenticationService;
        this.logoutService = logoutService;
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {

        return authenticationService.register(registerRequestDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {

        return authenticationService.login(loginRequestDto);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        logoutService.logout(request, response, authentication);

        return "Logout Successful";
    }
}
