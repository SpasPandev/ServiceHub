package com.example.servicehub.controller;

import com.example.servicehub.config.AppUser;
import com.example.servicehub.model.dto.CreatedServiceDto;
import com.example.servicehub.model.dto.ServiceCategoryDto;
import com.example.servicehub.model.dto.ServiceCategoryRequestDto;
import com.example.servicehub.model.dto.ServiceRequestDto;
import com.example.servicehub.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/create-service-category")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceCategoryDto> createServiceCategory(@Valid @RequestBody ServiceCategoryRequestDto serviceCategoryRequestDto) {

        return adminService.createServiceCategory(serviceCategoryRequestDto);
    }

    @PostMapping("/create-service")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreatedServiceDto> createService(@Valid @RequestBody ServiceRequestDto serviceRequestDto) {

        return adminService.createService(serviceRequestDto);
    }

    @PatchMapping("/assign-admin-role/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> assignAdminRole(@PathVariable Long userId) {

        return adminService.assignAdminRole(userId);
    }

    @PatchMapping("/remove-admin-role/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> removeAdminRole(@PathVariable Long userId,
                                                  @AuthenticationPrincipal AppUser appUser) {

        return adminService.removeAdminRole(userId, appUser);
    }

    @PatchMapping("/delete-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId,
                                             @AuthenticationPrincipal AppUser appUser) {

        return adminService.deleteUser(userId, appUser);
    }

    @PatchMapping("/restore-user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> restoreUser(@PathVariable Long userId,
                                              @AuthenticationPrincipal AppUser appUser) {

        return adminService.restoreUser(userId, appUser);
    }

}
