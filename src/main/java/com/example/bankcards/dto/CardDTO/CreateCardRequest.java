package com.example.bankcards.dto.CardDTO;

import jakarta.validation.constraints.*;

public record CreateCardRequest(
        @NotNull  Long userId,
        @NotNull  @Pattern(regexp = "\\d{13,19}") String panPlain,
        @NotNull @Size(max = 50) String ownerName,
        @NotNull @Max(12) Short expiryMonth,
        @NotNull @Pattern(regexp = "^[A-Z]{3}$")String currency,
        @NotNull  @Max(2100) @Min(2025) Short expiryYear
) {
}
