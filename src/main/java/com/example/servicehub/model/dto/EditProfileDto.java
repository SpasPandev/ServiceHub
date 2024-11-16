package com.example.servicehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EditProfileDto {

    private Long id;
    @NotBlank
    private String email;
    @NotBlank
    private String name;
    @NotEmpty
    private String password;
    @NotEmpty
    private String confirmPassword;
    @NotBlank
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")
    private String phoneNumber;

}
