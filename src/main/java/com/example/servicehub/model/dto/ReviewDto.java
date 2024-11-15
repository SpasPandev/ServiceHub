package com.example.servicehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class ReviewDto {

    @NotBlank
    private String nameOfUser;

    @NotBlank
    private String content;

    @NotBlank
    private Timestamp publishedAt;
}
