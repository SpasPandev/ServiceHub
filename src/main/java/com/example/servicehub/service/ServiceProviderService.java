package com.example.servicehub.service;

import com.example.servicehub.config.AppUser;
import com.example.servicehub.exception.ServiceProviderNotFoundException;
import com.example.servicehub.model.dto.AddServiceProviderRequestDto;
import com.example.servicehub.model.dto.ServiceDto;
import com.example.servicehub.model.dto.ServiceProviderRequestDto;
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
import java.util.List;
import java.util.stream.Collectors;

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

    public ResponseEntity<?> updateServiceProviderInfo(
            Long serviceProviderId, ServiceProviderRequestDto serviceProviderRequestDto,
            AppUser appUser) {

        ServiceProvider serviceProvider = serviceProviderRepository.findById(serviceProviderId).orElseThrow(() ->
                new ServiceProviderNotFoundException("ServiceProvider with id: " +
                        serviceProviderId + " was not found!"));

        User currentUser = userService.findUserByEmail(appUser.getUsername());

        if (!currentUser.getServiceProviders().contains(serviceProvider)) {

            return new ResponseEntity<>("This serviceProvider belongs to another user!",
                    HttpStatus.FORBIDDEN);
        }

        serviceProvider.setDescription(serviceProviderRequestDto.getDescription());
        serviceProvider.setLocation(serviceProviderRequestDto.getLocation());
        serviceProvider.setServiceEntity(serviceService.findServiceByServiceName(serviceProviderRequestDto.getServiceName()));
        serviceProvider.setProviderName(serviceProviderRequestDto.getProviderName());
        serviceProvider.setProviderEmail(serviceProviderRequestDto.getProviderEmail());
        serviceProvider.setProviderPhoneNumber(serviceProviderRequestDto.getProviderPhoneNumber());

        serviceProviderRepository.save(serviceProvider);

        return ResponseEntity.ok(modelMapper.map(serviceProvider, ServiceDto.class));
    }

    public ResponseEntity<?> findAllByLocation(String location) {

        List<ServiceProvider> serviceProviderList = serviceProviderRepository.findByLocationIgnoreCase(location);

        if (serviceProviderList.isEmpty()) {

            return new ResponseEntity<>(
                    "There is no serviceProvider for this location: " + location,
                    HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(
                serviceProviderList
                        .stream()
                        .map(e -> modelMapper.map(e, ServiceDto.class))
                        .toList()
        );
    }

    public ResponseEntity<?> findAllByServiceName(String serviceName) {

        List<ServiceProvider> serviceProviderList = serviceProviderRepository
                .findByServiceEntity_ServiceName(serviceName);

        if (serviceProviderList.isEmpty()) {

            return new ResponseEntity<>(
                    "There is no serviceProvider with this serviceName: " + serviceName,
                    HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(serviceProviderList
                .stream()
                .map(e -> modelMapper.map(e, ServiceDto.class))
                .toList());
    }

    public ResponseEntity<?> findAllByLocationAndService(String location, String serviceName) {

        List<ServiceProvider> serviceProviderList = serviceProviderRepository
                .findByLocationIgnoreCaseAndServiceEntity_ServiceName(location, serviceName);

        if (serviceProviderList.isEmpty()) {

            return new ResponseEntity<>(
                    "There is no serviceProvider with serviceName (" +
                            serviceName + ") in location: " + location,
                    HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(serviceProviderList
                .stream()
                .map(e -> modelMapper.map(e, ServiceDto.class))
                .toList());
    }

    public ResponseEntity<?> getMostLikedServiceProviders() {

        List<ServiceProvider> mostLikedProvidersList =
                serviceProviderRepository.findTopLikedProviders();

        if (mostLikedProvidersList.isEmpty()) {

            return new ResponseEntity<>(
                    "No serviceProvider liked",
                    HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(mostLikedProvidersList
                .stream()
                .map(e -> modelMapper.map(e, ServiceDto.class))
                .toList());
    }
}
