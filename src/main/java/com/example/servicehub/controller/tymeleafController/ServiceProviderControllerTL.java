package com.example.servicehub.controller.tymeleafController;

import com.example.servicehub.config.AppUser;
import com.example.servicehub.model.dto.LoginRequestDto;
import com.example.servicehub.model.dto.LoginResponseDto;
import com.example.servicehub.service.ServiceProviderService;
import com.example.servicehub.utils.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/service-provider")
public class ServiceProviderControllerTL {

    private final ServiceProviderService serviceProviderService;

    public ServiceProviderControllerTL(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    @GetMapping()
    public String showHomePage(
            @RequestParam(value = "sortByLikes", defaultValue = "false") boolean sortByLikes,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "serviceName", required = false) String serviceName,
            Model model) {

        ResponseEntity<?> all;

        if (sortByLikes) {
            all = serviceProviderService.getMostLikedServiceProviders();
        }
        else {
            all = serviceProviderService.findAll();
        }

        if (all.getStatusCode() == HttpStatus.NOT_FOUND) {
            model.addAttribute("hasServiceProviders", false);
            return "service-provider";
        }

        model.addAttribute("hasServiceProviders", true);
        model.addAttribute("allServiceProviders", all.getBody());
        model.addAttribute("isSortedByLikes", sortByLikes);
        model.addAttribute("servicesDropDown", serviceProviderService.getAllServiceNames().getBody());

        if (location != null && !location.trim().isEmpty() &&
                (serviceName == null || serviceName.trim().isEmpty())) {

            ResponseEntity<?> filteredProvidersByLocation = serviceProviderService.findAllByLocation(location);

            handleServiceProviderResponse(model, filteredProvidersByLocation);

        }
        else if (location != null && !location.trim().isEmpty()) {

            ResponseEntity<?> filteredProviders = serviceProviderService.findAllByLocationAndService(location, serviceName);

            handleServiceProviderResponse(model, filteredProviders);

        }
        else if (serviceName != null && !serviceName.trim().isEmpty()) {

            ResponseEntity<?> filteredProvidersByService = serviceProviderService.findAllByServiceName(serviceName);

            handleServiceProviderResponse(model, filteredProvidersByService);
        }

        return "service-provider";
    }

    @GetMapping("/view-info/{serviceProviderId}")
    public String showServiceProviderInfo(
            @PathVariable("serviceProviderId") Long serviceProviderId,
            @AuthenticationPrincipal AppUser appUser,
            Model model) {

        model.addAttribute("serviceProviderInfo",
                serviceProviderService.viewServiceProviderInfo(serviceProviderId).getBody());

        model.addAttribute("reviews",
                serviceProviderService.getReviewsByServiceProviderIdOrderByPublishedAtDesc(serviceProviderId));

        model.addAttribute("isAuthor",
                serviceProviderService.isAuthorOnServiceProvider(serviceProviderId, appUser.getUsername()));

        return "service-provider-info";
    }

    @PostMapping("/{id}/add-comment")
    public String addReview(
            @PathVariable("id") Long serviceProviderId,
            @RequestParam("content") String content,
            @AuthenticationPrincipal AppUser appUser,
            Model model) {

        ResponseEntity<?> response = serviceProviderService.addReview(serviceProviderId, appUser.getUsername(), content);

        if (response.getStatusCode() == HttpStatus.CREATED) {

            model.addAttribute("serviceProviderInfo", serviceProviderService.viewServiceProviderInfo(serviceProviderId).getBody());
            model.addAttribute("reviews", serviceProviderService.getReviewsByServiceProviderIdOrderByPublishedAtDesc(serviceProviderId));
            model.addAttribute("isAuthor", serviceProviderService.isAuthorOnServiceProvider(serviceProviderId, appUser.getUsername()));

            return "redirect:/service-provider/view-info/" + serviceProviderId;
        }
        else {
            model.addAttribute("error", "Could not add your review. Please try again.");
            return "service-provider-info";
        }
    }

    private void handleServiceProviderResponse(Model model, ResponseEntity<?> response) {

        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            model.addAttribute("noResultsFound", true);
            model.addAttribute("allServiceProviders", null);
        }
        else {
            model.addAttribute("allServiceProviders", response.getBody());
        }
    }

}