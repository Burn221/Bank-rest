package com.example.bankcards.dto.TransferDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

//todo
public record CreateTransferDifferentUserRequest(
        @NotNull Long fromCardId,
        @NotNull @Pattern(regexp = "^\\d{13,19}$") String toPan,
        @NotNull @Positive Long amount,
        @NotNull @Pattern(regexp = "^[A-Z]{3}$") String currency

) {
}
