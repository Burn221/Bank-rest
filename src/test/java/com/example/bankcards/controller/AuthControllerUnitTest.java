package com.example.bankcards.controller;

import com.example.bankcards.dto.JwtDTO.JwtAuthDto;
import com.example.bankcards.dto.JwtDTO.RefreshTokenDto;
import com.example.bankcards.dto.JwtDTO.UserCredentialsDto;
import com.example.bankcards.security.CustomUserDetailsService;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.service.Impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.naming.AuthenticationException;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerUnitTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean UserServiceImpl userService;
    @MockitoBean JwtService jwtService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;

    private JwtAuthDto jwt(String access, String refresh) {
        JwtAuthDto dto = new JwtAuthDto();
        dto.setToken(access);
        dto.setRefreshToken(refresh);
        return dto;
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/sign-in - 200 OK")
    void signIn_ok() throws Exception {
        when(userService.signIn(any(UserCredentialsDto.class)))
                .thenReturn(jwt("access-123", "refresh-456"));

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"username":"user","password":"user"}
                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("access-123"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-456"));

        verify(userService, times(1)).signIn(any(UserCredentialsDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/sign-in - AuthenticationException")
    void signIn_fail() throws Exception {
        when(userService.signIn(any(UserCredentialsDto.class)))
                .thenThrow(new AuthenticationException("bad creds"));

        mockMvc.perform(post("/api/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"username":"nikita","password":"wrong"}
                """))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).signIn(any(UserCredentialsDto.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/refresh - 200 OK")
    void refresh_ok() throws Exception {
        when(userService.refreshToken(any(RefreshTokenDto.class)))
                .thenReturn(jwt("access-new", "refresh-same"));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"refreshToken":"refresh-456"}
                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("access-new"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-same"));

        verify(userService, times(1)).refreshToken(any(RefreshTokenDto.class));
    }
}
