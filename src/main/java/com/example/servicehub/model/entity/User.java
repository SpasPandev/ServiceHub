package com.example.servicehub.model.entity;

import com.example.servicehub.model.enumeration.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class User extends BaseEntity {

    @Column(nullable = false)
    @NotBlank(message = "Please provide a name")
    private String name;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Please provide a email")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Please provide a password")
    private String password;

    @Column(nullable = false, length = 15)
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be between 10 and 15 digits")
    private String phoneNumber;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isProvider;

    @Column(nullable = false)
    private Timestamp registerAt;

    @OneToMany(mappedBy = "provider")
    private Set<ServiceProvider> serviceProviders;

    @OneToMany(mappedBy = "user")
    private List<Review> reviews;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(columnDefinition = "boolean default false", nullable = false)
    private boolean isDeleted;
}
