package com.example.bankcards.dto.CardDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateCardRequest(
        @NotNull @Size(max = 50) String ownerName
) {
}
