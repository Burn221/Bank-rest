package com.example.bankcards.dto.JwtDTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshTokenDto {
    @NotNull
    private String refreshToken;
}
