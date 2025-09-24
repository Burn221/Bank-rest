package com.example.bankcards.dto.CardDTO;

import com.example.bankcards.entity.enums.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardResponse{
    Long id;
    Long userId;
    String ownerName;
    String panMasked;
    String panPlain;
    Short expiryMonth;
    Short expiryYear;
    Status status;
    Long balanceMinor;
    String currency;
    LocalDateTime createdAt;
}
