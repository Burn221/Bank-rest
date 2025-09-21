package com.example.bankcards.dto;

public record BalanceResponse(
        Long currentBalance,
        String currency
) {

}
