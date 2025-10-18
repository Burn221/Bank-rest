package com.example.bankcards.dto.userdto;

import com.example.bankcards.entity.enums.Role;
import jakarta.validation.constraints.NotNull;


public record CreateUserRequest (
        @NotNull String username,
        @NotNull String password,
        @NotNull String confirmPassword
){



}
