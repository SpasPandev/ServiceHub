package com.example.servicehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreatedServiceDto {

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotBlank(message = "ServiceCategoryName is required")
    private String serviceCategoryName;

}
