package com.example.servicehub.controller.tymeleafController;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeControllerTL {

    @GetMapping("/home")
    public String showHomePage(Authentication authentication, Model model){

        model.addAttribute("namm", authentication.getName());
        return "home";
    }
}
