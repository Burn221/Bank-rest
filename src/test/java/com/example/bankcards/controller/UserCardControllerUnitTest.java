package com.example.bankcards.controller;

import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.CardDTO.CardResponse;
import com.example.bankcards.dto.CardDTO.CreateCardRequest;

import com.example.bankcards.dto.userdto.AuthUser;
import com.example.bankcards.entity.enums.Status;
import com.example.bankcards.security.CustomUserDetailsService;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.service.Impl.CardServiceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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


import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserCardController.class)


public class UserCardControllerUnitTest {

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    CardServiceImpl cardService;
    @MockitoBean
    JwtService jwtService;
    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

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

    @BeforeEach
    void setUpSecurity() {
        var ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(authToken());   // твой метод
        SecurityContextHolder.setContext(ctx);
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    private CardResponse cardSample() {
        CardResponse r = new CardResponse();
        r.setId(100L);
        r.setUserId(10L);
        r.setOwnerName("Nikita");
        r.setPanMasked("**** **** **** 1234");
        r.setStatus(Status.ACTIVE);
        r.setCurrency("KZT");
        r.setCreatedAt(LocalDateTime.now());
        return r;
    }


    @Test
    @DisplayName("POST /api/me/cards - 201 Created")
    void createMyCard() throws Exception {
        when(cardService.createCardUser(eq(10L), any(CreateCardRequest.class)))
                .thenReturn(cardSample());

        String body = """
        {
          "userId": 10,
          "ownerName": "Nikita",
          "currency": "KZT"
        }
        """;

        mockMvc.perform(post("/api/me/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/me/cards/100"))
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.currency").value("KZT"));

        verify(cardService, times(1)).createCardUser(eq(10L), any(CreateCardRequest.class));
    }

    @Test
    @DisplayName("GET /api/me/cards  200 OK (page)")
    void showMyCards() throws Exception {
        var r1 = cardSample();
        var r2 = cardSample(); r2.setId(101L);

        var page = new org.springframework.data.domain.PageImpl<>(
                java.util.List.of(r1, r2),
                org.springframework.data.domain.PageRequest.of(0, 2),
                2
        );

        when(cardService.showMyCardsUser(eq(10L), any())).thenReturn(page);

        mockMvc.perform(get("/api/me/cards")
                        .with(authentication(authToken()))
                        .queryParam("page", "0")
                        .queryParam("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(100))
                .andExpect(jsonPath("$.content[1].id").value(101))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.number").value(0));

        verify(cardService, times(1)).showMyCardsUser(eq(10L), any());
    }

    @Test
    @DisplayName("GET /api/me/cards/{cardId} -200 OK")
    void getMyCard() throws Exception {
        when(cardService.getMyCardUser(100L, 10L)).thenReturn(cardSample());

        mockMvc.perform(get("/api/me/cards/{cardId}", 100L)
                        .with(authentication(authToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.userId").value(10));

        verify(cardService, times(1)).getMyCardUser(100L, 10L);
    }

    @Test
    @DisplayName("GET /api/me/cards/balance/{cardId} - 200 OK")
    void getMyBalance() throws Exception {
        BalanceResponse br = new BalanceResponse(500_00L,"KZT");


        when(cardService.showMyBalanceUser(10L, 100L)).thenReturn(br);

        mockMvc.perform(get("/api/me/cards/balance/{cardId}", 100L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("KZT"))
                .andExpect(jsonPath("$.currentBalance").value(50000));

        verify(cardService, times(1)).showMyBalanceUser(10L, 100L);
    }

    @Test
    @DisplayName("PATCH /api/me/cards/block/{cardId} - 200 OK")
    void blockMyCard() throws Exception {
        var resp = cardSample();
        resp.setStatus(Status.BLOCKED);

        when(cardService.blockRequestUser(10L, 100L)).thenReturn(resp);

        mockMvc.perform(patch("/api/me/cards/block/{cardId}", 100L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("BLOCKED"));

        verify(cardService, times(1)).blockRequestUser(10L, 100L);
    }
}

