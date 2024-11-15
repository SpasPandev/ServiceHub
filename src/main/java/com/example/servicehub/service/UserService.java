package com.example.servicehub.service;

import com.example.servicehub.config.AppUser;
import com.example.servicehub.exception.UserDeletedException;
import com.example.servicehub.exception.UserNotFoundException;
import com.example.servicehub.exception.ValidationException;
import com.example.servicehub.model.dto.EditProfileDto;
import com.example.servicehub.model.dto.UpdatedProfileDto;
import com.example.servicehub.model.dto.UserDto;
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

    public ResponseEntity<?> getUserProfileById(Long userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {

            return new ResponseEntity<>("User with email: " +
                    userId + " was not found", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(modelMapper.map(user, UserDto.class));
    }

    public boolean isProviderWithId(Long userId) {

        return userRepository.findById(userId).orElseThrow(() ->
                        new UserNotFoundException("User with id: " + userId +
                                " was not found!"))
                .isProvider();
    }
}
