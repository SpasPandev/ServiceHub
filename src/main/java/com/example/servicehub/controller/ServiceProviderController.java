package com.example.servicehub.controller;

import com.example.servicehub.config.AppUser;
import com.example.servicehub.model.dto.AddServiceProviderRequestDto;
import com.example.servicehub.model.dto.ServiceDto;
import com.example.servicehub.service.ServiceProviderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/service-provider")
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;

    public ServiceProviderController(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    @PostMapping("/add-service-provider")
    public ResponseEntity<ServiceDto> addServiceProvider(
            @Valid @RequestBody AddServiceProviderRequestDto addServiceProviderRequestDto,
            @AuthenticationPrincipal AppUser appUser) {


        return serviceProviderService.addServiceProvider(addServiceProviderRequestDto, appUser);
    }

    @GetMapping("/view-service-provider-info/{serviceProviderId}")
    public ResponseEntity<ServiceDto> viewServiceProviderInfo(@PathVariable Long serviceProviderId) {

        return serviceProviderService.viewServiceProviderInfo(serviceProviderId);
    }

    @PatchMapping("/like-service-info/{serviceProviderId}")
    public ResponseEntity<ServiceDto> likeServiceProvider(@PathVariable Long serviceProviderId) {

        return serviceProviderService.likeServiceProvider(serviceProviderId);
    }

}
