package com.example.servicehub.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Service extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String serviceName;

    @ManyToOne
    @JoinColumn(name = "service_category_id", nullable = false)
    private ServiceCategory serviceCategory;

    @OneToMany(mappedBy = "service")
    private Set<ServiceProvider> serviceProviders;
}
