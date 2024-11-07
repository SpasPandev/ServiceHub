package com.example.servicehub.service;

import com.example.servicehub.config.AppUser;
import com.example.servicehub.exception.ServiceProviderNotFoundException;
import com.example.servicehub.model.dto.AddServiceProviderRequestDto;
import com.example.servicehub.model.dto.ServiceDto;
import com.example.servicehub.model.entity.ServiceProvider;
import com.example.servicehub.model.entity.User;
import com.example.servicehub.repository.ServiceProviderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;

@Service
public class ServiceProviderService {

    private final UserService userService;
    private final ServiceService serviceService;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ModelMapper modelMapper;

    public ServiceProviderService(UserService userService, ServiceService serviceService, ServiceProviderRepository serviceProviderRepository, ModelMapper modelMapper) {
        this.userService = userService;
        this.serviceService = serviceService;
        this.serviceProviderRepository = serviceProviderRepository;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<ServiceDto> addServiceProvider(AddServiceProviderRequestDto addServiceProviderRequestDto, AppUser appUser) {

        User currentUser = userService.findUserByEmail(appUser.getUsername());

        ServiceProvider serviceProvider = ServiceProvider
                .builder()
                .description(addServiceProviderRequestDto.getDescription())
                .likesCount(0)
                .location(addServiceProviderRequestDto.getLocation())
                .providerName(addServiceProviderRequestDto.getProviderName())
                .providerEmail(addServiceProviderRequestDto.getProviderEmail())
                .providerPhoneNumber(addServiceProviderRequestDto.getProviderPhoneNumber())
                .publishedAt(Timestamp.valueOf(LocalDateTime.now()))
                .provider(currentUser)
                .serviceEntity(serviceService.findServiceByServiceName(addServiceProviderRequestDto.getServiceName()))
                .reviews(new HashSet<>())
                .build();

        serviceProviderRepository.save(serviceProvider);

        if (!currentUser.isProvider()) {

            currentUser.setProvider(true);
            userService.saveUser(currentUser);
        }

        return new ResponseEntity<>(
                modelMapper.map(serviceProvider, ServiceDto.class),
                HttpStatus.CREATED);
    }

    public ResponseEntity<ServiceDto> viewServiceProviderInfo(Long serviceProviderId) {

        ServiceProvider serviceProvider = serviceProviderRepository
                .findById(serviceProviderId).orElseThrow(() ->
                        new ServiceProviderNotFoundException(
                                "ServiceProvider with id: " + serviceProviderId +
                                        " was not found!"));

        return ResponseEntity.ok(modelMapper.map(serviceProvider, ServiceDto.class));
    }


    public ResponseEntity<ServiceDto> likeServiceProvider(Long serviceProviderId) {

        ServiceProvider serviceProvider = serviceProviderRepository.findById(serviceProviderId).orElseThrow(() ->
                new ServiceProviderNotFoundException("ServiceProvider with id: " + serviceProviderId +
                        " was not found!"));

        serviceProvider.setLikesCount(serviceProvider.getLikesCount() + 1);
        serviceProviderRepository.save(serviceProvider);

        return ResponseEntity.ok(modelMapper.map(serviceProvider, ServiceDto.class));
    }
}
