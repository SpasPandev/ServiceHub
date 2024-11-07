package com.example.servicehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddServiceProviderRequestDto {

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotBlank(message = "Provider name is required")
    private String providerName;

    @NotBlank(message = "Provider email is required")
    private String providerEmail;

    @NotBlank(message = "Provider phoneNumber is required")
    private String providerPhoneNumber;

}
