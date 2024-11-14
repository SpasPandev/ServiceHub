package com.example.servicehub.controller.tymeleafController;

import com.example.servicehub.model.dto.*;
import com.example.servicehub.service.AuthenticationService;
import com.example.servicehub.service.LogoutService;
import com.example.servicehub.utils.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    private final LogoutService logoutService;

    public AuthenticationControllerTL(AuthenticationService authenticationService, LogoutService logoutService) {
        this.authenticationService = authenticationService;
        this.logoutService = logoutService;
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

        CookieUtils.setJwtCookie(response, loginResponseDto.getJwtToken());

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

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response,
                         Authentication authentication) {

        logoutService.logout(request, response, authentication);

        CookieUtils.clearJwtCookie(response);

        return "redirect:/login";
    }

}