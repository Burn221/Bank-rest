package com.example.bankcards.dto.CardDTO;

import jakarta.validation.constraints.NotNull;

public record BlockCardRequest(

        @NotNull String password,
        @NotNull String confirmPassword
) {
}
