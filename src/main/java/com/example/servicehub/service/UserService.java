package com.example.servicehub.service;

import com.example.servicehub.config.AppUser;
import com.example.servicehub.exception.UserNotFoundException;
import com.example.servicehub.exception.ValidationException;
import com.example.servicehub.model.dto.EditProfileDto;
import com.example.servicehub.model.dto.UpdatedProfileDto;
import com.example.servicehub.model.entity.User;
import com.example.servicehub.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper) {
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

        User currentUser = userRepository.findByEmail(appUser.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User with email: " +
                        appUser.getUsername() + " was not found"));

        currentUser.setEmail(editProfileDto.getEmail());
        currentUser.setName(editProfileDto.getName());
        currentUser.setPassword(passwordEncoder.encode(editProfileDto.getPassword()));
        currentUser.setPhoneNumber(editProfileDto.getPhoneNumber());

        userRepository.save(currentUser);

        return new ResponseEntity<>(
                modelMapper.map(currentUser, UpdatedProfileDto.class), HttpStatus.OK);
    }

}
