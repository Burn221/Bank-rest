package com.example.bankcards.dto.userdto;

import com.example.bankcards.entity.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    @NotNull String username;
    @NotNull Role role;
    @NotNull boolean enabled;
    @NotNull LocalDateTime createdAt;

}
