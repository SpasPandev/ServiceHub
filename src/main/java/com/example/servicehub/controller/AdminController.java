package com.example.servicehub.controller;

import com.example.servicehub.model.dto.ServiceCategoryDto;
import com.example.servicehub.model.dto.ServiceCategoryRequestDto;
import com.example.servicehub.model.dto.ServiceDto;
import com.example.servicehub.model.dto.ServiceRequestDto;
import com.example.servicehub.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/create-service-category")
    public ResponseEntity<ServiceCategoryDto> createServiceCategory(@Valid @RequestBody ServiceCategoryRequestDto serviceCategoryRequestDto){

        return adminService.createServiceCategory(serviceCategoryRequestDto);
    }

    @PostMapping("/create-service")
    public ResponseEntity<ServiceDto> createService(@Valid @RequestBody ServiceRequestDto serviceRequestDto){

        return adminService.createService(serviceRequestDto);
    }
}
