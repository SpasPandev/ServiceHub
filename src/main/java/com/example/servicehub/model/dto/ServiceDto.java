package com.example.servicehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
public class ServiceDto {

    @NotBlank
    private Long id;

    @NotBlank(message = "Description is required")
    private String description;

    @PositiveOrZero(message = "Likes count must be zero or positive.")
    private int likesCount;

    @NotNull(message = "Published date is required.")
    private Timestamp publishedAt;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotBlank(message = "ServiceCategoryName is required")
    private String serviceCategoryName;

    @NotBlank(message = "Provider name should not be blank")
    private String providerName;

    @NotBlank(message = "Provider email should not be blank")
    private String providerEmail;

    @NotBlank(message = "Provider phone number should not be blank")
    private String providerPhoneNumber;

    @NotNull(message = "Reviews list cannot be null.")
    private List<ReviewDto> reviews;

    @NotBlank
    private Long providerId;

}
