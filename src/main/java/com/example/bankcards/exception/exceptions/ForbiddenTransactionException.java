package com.example.bankcards.exception.exceptions;

public class ForbiddenTransactionException extends RuntimeException {
    public ForbiddenTransactionException(String message) {
        super(message);
    }
}
