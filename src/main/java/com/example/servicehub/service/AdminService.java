package com.example.servicehub.service;

import com.example.servicehub.config.AppUser;
import com.example.servicehub.model.dto.ServiceCategoryDto;
import com.example.servicehub.model.dto.ServiceCategoryRequestDto;
import com.example.servicehub.model.dto.ServiceDto;
import com.example.servicehub.model.dto.ServiceRequestDto;
import com.example.servicehub.model.entity.ServiceCategory;
import com.example.servicehub.model.entity.User;
import com.example.servicehub.model.enumeration.Role;
import com.example.servicehub.repository.ServiceCategoryRepository;
import com.example.servicehub.repository.ServiceRepository;
import jakarta.persistence.EntityExistsException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
public class AdminService {

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ServiceRepository serviceRepository;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final ModelMapper modelMapper;

    public AdminService(ServiceCategoryRepository serviceCategoryRepository, ServiceRepository serviceRepository, UserService userService, AuthenticationService authenticationService, ModelMapper modelMapper) {
        this.serviceCategoryRepository = serviceCategoryRepository;
        this.serviceRepository = serviceRepository;
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.modelMapper = modelMapper;
    }


    public ResponseEntity<ServiceCategoryDto> createServiceCategory(
            ServiceCategoryRequestDto serviceCategoryRequestDto) {

        Optional<ServiceCategory> serviceCategoryOpt =
                serviceCategoryRepository.findByName(serviceCategoryRequestDto.getName());

        if (serviceCategoryOpt.isPresent()) {

            throw new EntityExistsException("ServiceCategory with name: " +
                    serviceCategoryRequestDto.getName() + " already exist!");
        }

        ServiceCategory savedServiceCategory = serviceCategoryRepository
                .save(ServiceCategory
                        .builder()
                        .name(serviceCategoryRequestDto.getName())
                        .services(new HashSet<>())
                        .build());

        return new ResponseEntity<>(modelMapper.map(savedServiceCategory, ServiceCategoryDto.class),
                HttpStatus.CREATED);
    }

    public ResponseEntity<ServiceDto> createService(ServiceRequestDto serviceRequestDto) {

        Optional<com.example.servicehub.model.entity.Service> serviceOpt =
                serviceRepository.findByServiceName(serviceRequestDto.getServiceName());

        if (serviceOpt.isPresent()) {

            throw new EntityExistsException("Service with name: " +
                    serviceRequestDto.getServiceName() + " already exist!");
        }

        Optional<ServiceCategory> serviceCategoryOpt =
                serviceCategoryRepository.findByName(serviceRequestDto.getServiceCategoryName());

        if (serviceCategoryOpt.isEmpty()) {

            throw new EntityExistsException("ServiceCategory with name: " +
                    serviceRequestDto.getServiceCategoryName() + " did not exist!");

        }

        com.example.servicehub.model.entity.Service savedService =
                serviceRepository.save(com.example.servicehub.model.entity.Service
                        .builder()
                        .serviceName(serviceRequestDto.getServiceName())
                        .serviceCategory(serviceCategoryOpt.get())
                        .serviceProviders(new HashSet<>())
                        .build());

        return new ResponseEntity<>(modelMapper.map(savedService, ServiceDto.class), HttpStatus.CREATED);
    }

    public ResponseEntity<String> assignAdminRole(Long userId) {

        User foundedUser = userService.findUserById(userId);

        if (foundedUser.getRole().equals(Role.ADMIN)) {
            return ResponseEntity.ok("This user is already with ADMIN role");
        }

        foundedUser.setRole(Role.ADMIN);

        userService.saveUser(foundedUser);

        return ResponseEntity.ok("Role Admin assigned successfully!");
    }

    public ResponseEntity<String> removeAdminRole(Long userId, AppUser appUser) {

        User currentUser = userService.findUserByEmail(appUser.getUsername());

        if (currentUser.getId().equals(userId)) {
            return new ResponseEntity<>("You can't change your own role!", HttpStatus.BAD_REQUEST);

        }

        User foundedUser = userService.findUserById(userId);

        if (foundedUser.getRole().equals(Role.USER)) {
            return ResponseEntity.ok("This user is already with USER role");
        }

        foundedUser.setRole(Role.USER);

        userService.saveUser(foundedUser);

        return ResponseEntity.ok("Role Admin removed successfully!");
    }

    public ResponseEntity<String> deleteUser(Long userId, AppUser appUser) {

        User currentUser = userService.findUserByEmail(appUser.getUsername());

        if (currentUser.getId().equals(userId)) {

            return new ResponseEntity<>("You cannot delete your own account!",
                    HttpStatus.FORBIDDEN);
        }

        User foundedUser = userService.findUserById(userId);

        if (foundedUser.isDeleted()) {

            return new ResponseEntity<>("This user is already deleted!",
                    HttpStatus.NOT_FOUND);
        }

        foundedUser.setDeleted(true);
        authenticationService.revokeAllUserTokens(foundedUser);
        userService.saveUser(foundedUser);

        return ResponseEntity.ok("User is deleted successfully!");
    }

    public ResponseEntity<String> restoreUser(Long userId, AppUser appUser) {

        User currentUser = userService.findUserByEmail(appUser.getUsername());

        if (currentUser.getId().equals(userId)) {

            return new ResponseEntity<>("You cannot restore your own account!",
                    HttpStatus.FORBIDDEN);
        }

        User foundedUser = userService.findUserById(userId);

        if (!foundedUser.isDeleted()) {

            return ResponseEntity.ok("This user is not deleted!");
        }

        foundedUser.setDeleted(false);
        userService.saveUser(foundedUser);

        return ResponseEntity.ok("User restored successfully!");
    }
}
