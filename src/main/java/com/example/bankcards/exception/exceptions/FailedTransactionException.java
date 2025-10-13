package com.example.bankcards.exception.exceptions;

public class FailedTransactionException extends RuntimeException {
    public FailedTransactionException(String message) {
        super(message);
    }
}
