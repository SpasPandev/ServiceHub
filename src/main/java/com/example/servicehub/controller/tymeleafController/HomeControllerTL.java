package com.example.servicehub.controller.tymeleafController;

import com.example.servicehub.service.ServiceProviderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeControllerTL {

    private final ServiceProviderService serviceProviderService;

    public HomeControllerTL(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    @GetMapping("/home")
    public String showHomePage(
            @RequestParam(value = "sortByLikes", defaultValue = "false") boolean sortByLikes, Model model){

        ResponseEntity<?> all;

        if (sortByLikes) {

            all = serviceProviderService.getMostLikedServiceProviders();
        } else {

            all = serviceProviderService.findAll();
        }

        if (all.getStatusCode() == HttpStatus.NOT_FOUND) {

            model.addAttribute("hasServiceProviders", false);
            return "home";
        }

        model.addAttribute("hasServiceProviders", true);
        model.addAttribute("allServiceProviders", all.getBody());

        model.addAttribute("isSortedByLikes", sortByLikes);

        return "home";
    }
}
