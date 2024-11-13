package com.example.servicehub.controller.tymeleafController;

import com.example.servicehub.model.dto.*;
import com.example.servicehub.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthenticationControllerTL {

    private final AuthenticationService authenticationService;

    public AuthenticationControllerTL(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @ModelAttribute
    public RegisterRequestDto registerRequestDto() {
        return new RegisterRequestDto();
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

    @GetMapping("/register")
    public String register() {

        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid RegisterRequestDto registerRequestDto,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            redirectAttributes
                    .addFlashAttribute("registerRequestDto", registerRequestDto);
            redirectAttributes
                    .addFlashAttribute("org.springframework.validation.BindingResult.registerRequestDto", bindingResult);

            return "redirect:/register";
        }

        ResponseEntity<?> registerResponseEntity =
                authenticationService.register(registerRequestDto);

        if (registerResponseEntity.getStatusCode() == HttpStatus.CONFLICT) {

            redirectAttributes
                    .addFlashAttribute("emailExistsError", true)
                    .addFlashAttribute("registerRequestDto", registerRequestDto);

            return "redirect:/register";
        }

        if (registerResponseEntity.getStatusCode() == HttpStatus.CREATED) {

            return "redirect:/login";
        } else {

            redirectAttributes
                    .addFlashAttribute("showPasswordsDontMatchError", true)
                    .addFlashAttribute("registerRequestDto", registerRequestDto);

            return "redirect:/register";
        }
    }

}