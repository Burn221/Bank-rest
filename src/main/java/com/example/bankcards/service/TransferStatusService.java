package com.example.bankcards.service;

public interface TransferStatusService {

    void markFailed(Long fromId, Long toId, long amountMinor, String currency);
}
