package com.example.bankcards.dto.CardDTO;

import jakarta.validation.constraints.*;

public record CreateCardRequest(

        @NotNull  Long userId,
        @NotNull @Size(max = 50) String ownerName,

        @NotNull @Pattern(regexp = "^[A-Z]{3}$")String currency

) {
}
