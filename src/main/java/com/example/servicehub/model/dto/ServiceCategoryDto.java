package com.example.servicehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceCategoryDto {

    @NotBlank(message = "Category name is required")
    private String name;

}
