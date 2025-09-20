package com.example.bankcards.dto.CardDTO;

import java.time.LocalDateTime;

public record CardResponse(
        Long id,
        Long userId,
        String panMasked,
        String ownerName,
        Short expiryMonth,
        Short expiryYear,
        String status,
        Long balanceMinor,
        String currency,
        LocalDateTime createdAt


) {
}
