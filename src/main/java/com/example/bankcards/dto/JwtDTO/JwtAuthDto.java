package com.example.bankcards.dto.JwtDTO;

import lombok.Data;

@Data
public class JwtAuthDto {

    private String token;
    private String refreshToken;
}
