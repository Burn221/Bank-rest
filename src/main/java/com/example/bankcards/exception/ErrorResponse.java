package com.example.bankcards.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private String uri;
    private int code;
    private LocalDateTime timestamp;
}
