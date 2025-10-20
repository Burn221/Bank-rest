package com.example.bankcards.dto.TransferDTO;

import com.example.bankcards.entity.enums.TransferStatus;

import java.time.LocalDateTime;

//todo поменять на class и добавить кэширование
public record TransferResponse (
        Long id,
        Long fromCardId,
        Long toCardId,
        Long amountMinor,
        String currency,
        TransferStatus transferStatus,
        LocalDateTime createdAt

) {
}
