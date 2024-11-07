package com.example.servicehub.config;

import com.example.servicehub.model.dto.ServiceDto;
import com.example.servicehub.model.dto.UpdatedProfileDto;
import com.example.servicehub.model.entity.ServiceProvider;
import com.example.servicehub.model.entity.User;
import com.example.servicehub.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;

    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> new AppUser(userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with email: " +
                                username + " was not found")));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        modelMapper
                .typeMap(User.class, UpdatedProfileDto.class)
                .addMappings(mapper ->
                        mapper.map(User::getEmail, UpdatedProfileDto::setEmail))
                .addMappings(mapper ->
                        mapper.map(User::getName, UpdatedProfileDto::setName))
                .addMappings(mapper ->
                        mapper.map(User::getPhoneNumber, UpdatedProfileDto::setPhoneNumber))
        ;

        modelMapper
                .addMappings(new PropertyMap<ServiceProvider, ServiceDto>() {
                    @Override
                    protected void configure() {

                        map(source.getDescription(), destination.getDescription());
                        map(source.getLikesCount(), destination.getLikesCount());
                        map(source.getPublishedAt(), destination.getPublishedAt());
                        map(source.getLocation(), destination.getLocation());
                        map(source.getServiceEntity().getServiceName(), destination.getServiceName());
                        map(source.getServiceEntity().getServiceCategory().getName(), destination.getServiceCategoryName());
                        map(source.getProvider().getName(), destination.getProviderName());
                        map(source.getProvider().getEmail(), destination.getProviderEmail());
                        map(source.getProvider().getPhoneNumber(), destination.getProviderPhoneNumber());
                        map(source.getReviews(), destination.getReviews());
                    }
                });

        return modelMapper;
    }

}
