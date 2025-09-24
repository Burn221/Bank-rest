package com.example.bankcards.controller;

import com.example.bankcards.dto.JwtDTO.JwtAuthDto;
import com.example.bankcards.dto.JwtDTO.RefreshTokenDto;
import com.example.bankcards.dto.JwtDTO.UserCredentialsDto;
import com.example.bankcards.service.Impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
@Validated
@Tag(name="Authentication Controller", description = "Controller for authentication and token refreshing")
public class AuthController {

    private UserServiceImpl userService;


    @Operation(summary = "Sign in using JWT token")
    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthDto> signIn(@RequestBody UserCredentialsDto dto) throws AuthenticationException{

        try{
            JwtAuthDto jwtAuthDto= userService.signIn(dto);
            return ResponseEntity.ok(jwtAuthDto);

        }
        catch (AuthenticationException e){
            throw new AuthenticationException("Authentication failed "+ e.getMessage());

        }

    }

    @Operation(summary = "Refresh JWT token")
    @PostMapping("/refresh")
    public JwtAuthDto refresh(@RequestBody RefreshTokenDto dto) throws Exception{

        return userService.refreshToken(dto);

    }

}
