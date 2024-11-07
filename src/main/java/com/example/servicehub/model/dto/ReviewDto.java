package com.example.servicehub.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewDto {

    @NotBlank
    private String nameOfUser;

    @NotBlank
    private String content;

    @NotBlank
    private String publishedAt;
}
