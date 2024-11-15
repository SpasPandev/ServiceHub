package com.example.servicehub.controller.tymeleafController;

import com.example.servicehub.config.AppUser;
import com.example.servicehub.service.ServiceProviderService;
import com.example.servicehub.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller()
public class UserControllerTL {

    private final UserService userService;
    private final ServiceProviderService serviceProviderService;

    public UserControllerTL(UserService userService, ServiceProviderService serviceProviderService) {
        this.userService = userService;
        this.serviceProviderService = serviceProviderService;
    }

    @GetMapping("/user/profile/{userId}")
    public String viewProfile(@PathVariable Long userId,
                              @AuthenticationPrincipal AppUser appUser,
                              Model model) {

        model.addAttribute("user", userService.getUserProfileById(userId).getBody());
        model.addAttribute("currentUserEmail", appUser.getUsername());

        boolean isServiceProvider = userService.isProviderWithId(userId);

        if (isServiceProvider) {
            model.addAttribute("serviceProviders", serviceProviderService.getServiceProvidersByUserId(userId));
        }
        else {
            model.addAttribute("noServiceMessage", "This user has not provided any services yet.");
        }

        return "profile";
    }
}
