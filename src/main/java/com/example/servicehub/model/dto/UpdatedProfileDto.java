package com.example.servicehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdatedProfileDto {


    @NotBlank
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    private String phoneNumber;

}
