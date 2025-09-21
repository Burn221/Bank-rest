package com.example.bankcards.dto.TransferDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record CreateTransferRequest(

        @NotNull Long fromCard,
        @NotNull Long toCard,
        @NotNull Long amountMinor,
        @NotNull @Pattern(regexp = "^[A-Z]{3}$") String currency


) {
}
