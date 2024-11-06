package com.example.servicehub.model.dto;

import com.example.servicehub.model.entity.ServiceCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceDto {

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotBlank(message = "ServiceCategoryName is required")
    private String serviceCategoryName;

}
