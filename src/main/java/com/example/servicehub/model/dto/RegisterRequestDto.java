package com.example.servicehub.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequestDto {

    private String email;
    private String name;
    private String password;
    private String phoneNumber;

}
