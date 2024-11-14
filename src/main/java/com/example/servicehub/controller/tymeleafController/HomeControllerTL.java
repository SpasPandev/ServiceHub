package com.example.servicehub.controller.tymeleafController;

import com.example.servicehub.service.ServiceProviderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeControllerTL {

    private final ServiceProviderService serviceProviderService;

    public HomeControllerTL(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    @GetMapping("/home")
    public String showHomePage(Authentication authentication, Model model){

        ResponseEntity<?> all = serviceProviderService.findAll();

        Object body = all.getBody();

        if (all.getStatusCode() == HttpStatus.NOT_FOUND){

            model.addAttribute("hasServiceProviders", false);
            return "home";
        }
        model.addAttribute("hasServiceProviders", true);
        model.addAttribute("allServiceProviders", all.getBody());

        return "home";
    }
}
