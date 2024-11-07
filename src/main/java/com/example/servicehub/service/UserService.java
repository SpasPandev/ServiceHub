package com.example.servicehub.service;

import com.example.servicehub.config.AppUser;
import com.example.servicehub.exception.UserDeletedException;
import com.example.servicehub.exception.UserNotFoundException;
import com.example.servicehub.exception.ValidationException;
import com.example.servicehub.model.dto.AddServiceProviderRequestDto;
import com.example.servicehub.model.dto.EditProfileDto;
import com.example.servicehub.model.dto.ServiceDto;
import com.example.servicehub.model.dto.UpdatedProfileDto;
import com.example.servicehub.model.entity.ServiceProvider;
import com.example.servicehub.model.entity.User;
import com.example.servicehub.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;

@Service
public class UserService {

    private final ServiceService serviceService;
    private final ServiceProviderService serviceProviderService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserService(ServiceService serviceService, ServiceProviderService serviceProviderService, UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
        this.serviceService = serviceService;
        this.serviceProviderService = serviceProviderService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    public ResponseEntity<UpdatedProfileDto> updateProfile(EditProfileDto editProfileDto, AppUser appUser) {

        if (!editProfileDto.getPassword().equals(editProfileDto.getConfirmPassword())) {

            throw new ValidationException("Passwords are not equal!");
        }

        if (userRepository.findByEmail(editProfileDto.getEmail()).isPresent() &&
                !editProfileDto.getEmail().equals(appUser.getUsername())) {

            throw new ValidationException("This email is already used!");
        }

        User currentUser = getCurrentUser(appUser);

        currentUser.setEmail(editProfileDto.getEmail());
        currentUser.setName(editProfileDto.getName());
        currentUser.setPassword(passwordEncoder.encode(editProfileDto.getPassword()));
        currentUser.setPhoneNumber(editProfileDto.getPhoneNumber());

        userRepository.save(currentUser);

        return new ResponseEntity<>(
                modelMapper.map(currentUser, UpdatedProfileDto.class), HttpStatus.OK);
    }

    public User findUserById(Long userId) {

        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("User with id: " + userId +
                        " was not found!"));
    }

    public void saveUser(User user) {

        userRepository.save(user);
    }

    public User findUserByEmail(String email) {

        return userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User with email: " + email +
                        " was not found!"));
    }

    public ResponseEntity<ServiceDto> addServiceProvider(AddServiceProviderRequestDto addServiceProviderRequestDto, AppUser appUser) {

        User currentUser = getCurrentUser(appUser);

        checkIfUserIsDeleted(currentUser);

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

        serviceProviderService.saveServiceProvider(serviceProvider);

        if (!currentUser.isProvider()) {

            currentUser.setProvider(true);
            userRepository.save(currentUser);
        }

        return new ResponseEntity<>(
                modelMapper.map(serviceProvider, ServiceDto.class),
                HttpStatus.CREATED);
    }

    private static void checkIfUserIsDeleted(User currentUser) {

        if (currentUser.isDeleted()) {

            throw new UserDeletedException("The user account has been deleted and is no longer available.");
        }
    }

    private User getCurrentUser(AppUser appUser) {

        return userRepository.findByEmail(appUser.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User with email: " +
                        appUser.getUsername() + " was not found"));
    }

    public ResponseEntity<ServiceDto> viewServiceProviderInfo(Long serviceProviderId) {

        return ResponseEntity.ok(modelMapper.map(serviceProviderService.findServiceProviderById(serviceProviderId), ServiceDto.class));
    }

    public ResponseEntity<ServiceDto> likeServiceProvider(Long serviceProviderId) {

        return ResponseEntity.ok(modelMapper.map(
                serviceProviderService.likeServiceProvider(serviceProviderId),
                ServiceDto.class));
    }
}
