package com.example.servicehub.model.dto;

import com.example.servicehub.model.entity.ServiceCategory;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceRequestDto {

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotBlank(message = "ServiceCategoryName is required")
    private String serviceCategoryName;

}
