package com.example.servicehub.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "service")
public class ServiceEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String serviceName;

    @ManyToOne
    @JoinColumn(name = "service_category_id", nullable = false)
    private ServiceCategory serviceCategory;

    @OneToMany(mappedBy = "serviceEntity")
    private Set<ServiceProvider> serviceProviders;
}
