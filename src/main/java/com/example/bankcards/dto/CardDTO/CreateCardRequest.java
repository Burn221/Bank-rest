package com.example.bankcards.dto.CardDTO;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

public record CreateCardRequest(

        @Nullable Long userId,
        @NotNull @Size(max = 50) String ownerName,

        @NotNull @Pattern(regexp = "^[A-Z]{3}$")String currency

) {
}
