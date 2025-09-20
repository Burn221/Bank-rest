package com.example.bankcards.dto.TransferDTO;

import java.time.LocalDateTime;

public record TransferResponse(
        Long id,
        Long fromCardId,
        Long toCardId,
        Long amountMinor,
        String currency,
        String transferStatus,
        LocalDateTime createdAt

) {
}
