package com.example.bankcards.controller;
import com.example.bankcards.dto.TransferDTO.CreateTransferRequest;
import com.example.bankcards.dto.TransferDTO.TransferResponse;
import com.example.bankcards.dto.userdto.AuthUser;
import com.example.bankcards.entity.enums.TransferStatus;
import com.example.bankcards.security.CustomUserDetailsService;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.service.Impl.TransferServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserTransfersController.class)
public class UserTransferControllerUnitTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean
    TransferServiceImpl transferService;
    @MockitoBean JwtService jwtService;
    @MockitoBean CustomUserDetailsService customUserDetailsService;

    private AuthUser me() {
        return new AuthUser(
                10L,
                "user",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    private UsernamePasswordAuthenticationToken authToken() {
        return new UsernamePasswordAuthenticationToken(
                me(), "N/A", me().authorities()
        );
    }

    private TransferResponse sampleTransfer() {
        TransferResponse r = new TransferResponse(200L,100L,101L,10_000L,"KZT", TransferStatus.SUCCESS, LocalDateTime.now());
        return r;
    }
    @BeforeEach
    void setupSecurity() {
        SecurityContextHolder.getContext().setAuthentication(authToken());
    }

    @Test
    @DisplayName("POST /api/me/transfers 201 Created")
    void executeTransfer() throws Exception {

        CreateTransferRequest request = new CreateTransferRequest(100L, 101L, 10_000L,"KZT");
        when(transferService.executeTransfer(eq(10L), any(CreateTransferRequest.class)))
                .thenReturn(sampleTransfer());


        String body = """
        {
          "fromCard": 100,
          "toCard": 101,
          "amountMinor": 10000,
          "currency": "KZT"
        
        }
        """;

        // вызов эндпоинта
        mockMvc.perform(MockMvcRequestBuilders.post("/api/me/transfers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/me/transfers/200"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fromCardId").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$.toCardId").value(101))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency").value("KZT"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.amountMinor").value(10000));

        verify(transferService, times(1))
                .executeTransfer(eq(10L), any(CreateTransferRequest.class));
    }
}
