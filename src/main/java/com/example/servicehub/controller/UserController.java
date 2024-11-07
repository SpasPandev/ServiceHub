package com.example.servicehub.controller;

import com.example.servicehub.config.AppUser;
import com.example.servicehub.model.dto.EditProfileDto;
import com.example.servicehub.model.dto.OfferServiceRequestDto;
import com.example.servicehub.model.dto.ServiceDto;
import com.example.servicehub.model.dto.UpdatedProfileDto;
import com.example.servicehub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<UpdatedProfileDto> updateProfile(@Valid @RequestBody EditProfileDto editProfileDto,
                                                           @AuthenticationPrincipal AppUser appUser) {

        return userService.updateProfile(editProfileDto, appUser);
    }

    @PostMapping("/offer-service")
    public ResponseEntity<ServiceDto> offerService(
            @Valid @RequestBody OfferServiceRequestDto offerServiceRequestDto,
            @AuthenticationPrincipal AppUser appUser) {


        return userService.offerService(offerServiceRequestDto, appUser);
    }

    @GetMapping("/view-service-provider-info/{serviceProviderId}")
    public ResponseEntity<ServiceDto> viewServiceProviderInfo(@PathVariable Long serviceProviderId){

        return userService.viewServiceProviderInfo(serviceProviderId);
    }

}
