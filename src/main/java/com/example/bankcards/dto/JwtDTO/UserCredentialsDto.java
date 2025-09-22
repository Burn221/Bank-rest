package com.example.bankcards.dto.JwtDTO;

import lombok.Data;

@Data
public class UserCredentialsDto {
    private String username;
    private String password;
}
