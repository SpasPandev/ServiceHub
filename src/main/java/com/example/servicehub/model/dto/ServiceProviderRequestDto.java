package com.example.servicehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ServiceProviderRequestDto {

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotBlank(message = "Provider name should not be blank")
    private String providerName;

    @NotBlank(message = "Provider email should not be blank")
    private String providerEmail;

    @NotBlank(message = "Provider phone number should not be blank")
    private String providerPhoneNumber;

}
