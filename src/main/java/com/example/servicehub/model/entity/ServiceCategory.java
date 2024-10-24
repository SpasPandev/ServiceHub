package com.example.servicehub.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ServiceCategory extends BaseEntity {

    @Column(nullable = false)
    @NotBlank(message = "Category name is required")
    private String name;

    @OneToMany(mappedBy = "serviceCategory")
    private Set<Service> services;

}
