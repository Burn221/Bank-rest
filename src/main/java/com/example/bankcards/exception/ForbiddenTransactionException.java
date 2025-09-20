package com.example.bankcards.exception;

public class ForbiddenTransactionException extends RuntimeException {
    public ForbiddenTransactionException(String message) {
        super(message);
    }
}
