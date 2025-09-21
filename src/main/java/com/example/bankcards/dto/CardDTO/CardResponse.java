package com.example.bankcards.dto.CardDTO;

import jdk.jfr.DataAmount;
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
    String status;
    Long balanceMinor;
    String currency;
    LocalDateTime createdAt;
}
