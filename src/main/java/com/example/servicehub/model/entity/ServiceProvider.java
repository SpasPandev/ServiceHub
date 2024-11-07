package com.example.servicehub.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
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
    @NotBlank(message = "Please provide a location")
    private String location;

    @Column(columnDefinition = "int default 0", nullable = false)
    private int likesCount;

    @Column(nullable = false)
    private Timestamp publishedAt;

    @OneToMany(mappedBy = "serviceProvider")
    private Set<Review> reviews;
}
