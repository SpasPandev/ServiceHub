package com.example.servicehub.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ServiceProvider extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity serviceEntity;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    @NotBlank(message = "Please provide a provider name")
    private String providerName;

    @Column(nullable = false)
    @NotBlank(message = "Please provide a provider email")
    private String providerEmail;

    @Column(nullable = false, length = 15)
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")
    private String providerPhoneNumber;

    @Column(nullable = false)
    @NotBlank(message = "Please provide a location")
    private String location;

    @Column(columnDefinition = "int default 0", nullable = false)
    private int likesCount;

    @Column(nullable = false)
    private Timestamp publishedAt;

    @OneToMany(mappedBy = "serviceProvider")
    private List<Review> reviews;
}
