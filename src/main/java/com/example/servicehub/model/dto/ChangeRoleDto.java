package com.example.servicehub.model.dto;

import com.example.servicehub.model.enumeration.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChangeRoleDto {

    @NotNull
    private Role role;
}
