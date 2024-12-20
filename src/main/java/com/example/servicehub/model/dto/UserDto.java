package com.example.servicehub.model.dto;

import com.example.servicehub.model.enumeration.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class UserDto {

    @NotBlank
    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    private boolean isDeleted;

    @NotBlank
    private boolean isProvider;

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private Timestamp registerAt;

    private Role role;

}
