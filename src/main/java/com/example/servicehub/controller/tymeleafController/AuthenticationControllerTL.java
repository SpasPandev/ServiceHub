package com.example.servicehub.controller.tymeleafController;

import com.example.servicehub.model.dto.LoginRequestDto;
import com.example.servicehub.model.dto.LoginResponseDto;
import com.example.servicehub.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationControllerTL {

    private final AuthenticationService authenticationService;

    public AuthenticationControllerTL(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            LoginRequestDto loginRequestDto, HttpServletResponse response, Model model) {

        LoginResponseDto loginResponseDto = authenticationService.login(loginRequestDto).getBody();


        Cookie jwtCookie = new Cookie("jwtToken", loginResponseDto.getJwtToken());
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60 * 24);
        response.addCookie(jwtCookie);

        return "redirect:/home";
    }

}