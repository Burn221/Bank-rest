package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@Valid
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username",unique = true, nullable = false, length = 100)
    private String username;

    @Column(name="password_hash", nullable = false)
    private String password_hash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "enabled", nullable = false)
    private boolean enabled=true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;









}
