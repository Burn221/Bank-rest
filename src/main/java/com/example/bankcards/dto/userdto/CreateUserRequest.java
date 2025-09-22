package com.example.bankcards.dto.userdto;

import com.example.bankcards.entity.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

public record CreateUserRequest (
        @NotNull String username,
        @NotNull String password,
        @NotNull Role role
){



}
