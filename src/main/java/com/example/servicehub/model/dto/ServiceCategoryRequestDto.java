package com.example.servicehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceCategoryRequestDto {

    @NotBlank(message = "Category name is required")
    private String name;

}
