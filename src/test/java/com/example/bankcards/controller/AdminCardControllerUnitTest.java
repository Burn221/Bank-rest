package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDTO.CardResponse;
import com.example.bankcards.dto.CardDTO.CreateCardRequest;
import com.example.bankcards.dto.CardDTO.UpdateCardRequest;
import com.example.bankcards.entity.enums.Status;
import com.example.bankcards.security.CustomUserDetailsService;
import com.example.bankcards.security.jwt.JwtService;
import com.example.bankcards.service.Impl.CardServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;


import java.time.LocalDateTime;
import java.util.List;


import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AdminCardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminCardControllerUnitTest {


    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    CardServiceImpl cardService;
    @MockitoBean
    JwtService jwtService;
    @MockitoBean
    CustomUserDetailsService customUserDetailsService;


    private CardResponse sample() {
        CardResponse r = new CardResponse();
        r.setId(10L);
        r.setUserId(10L);
        r.setOwnerName("Nikita");
        r.setPanMasked("**** **** **** 1234");
        r.setStatus(Status.ACTIVE);
        r.setCurrency("KZT");
        r.setCreatedAt(LocalDateTime.now());
        return r;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/admin/cards/create - 201 Created")
    void createCard() throws Exception {
        CreateCardRequest dto = new CreateCardRequest(10L, "Nikita", "KZT");
        when(cardService.createCardAdmin(dto)).thenReturn(sample());
        mockMvc.perform(post("/api/admin/cards/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": 10,
                                
                                  "ownerName": "Nikita",
                                  "currency": "KZT"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.ownerName").value("Nikita"))
                .andExpect(jsonPath("$.currency").value("KZT"));
        ;

        verify(cardService, times(1)).createCardAdmin(dto);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/admin/cards/update/{id} - 200 OK")
    void updateCard() throws Exception {
        Long cardId = 10L;
        UpdateCardRequest dto = new UpdateCardRequest("Nikita2");

        CardResponse resp = sample();
        resp.setOwnerName("Nikita2");

        when(cardService.updateCardAdmin(cardId, dto)).thenReturn(resp);

        mockMvc.perform(put("/api/admin/cards/update/{id}", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "ownerName": "Nikita2"
                                    }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.ownerName").value("Nikita2"));


        verify(cardService, times(1)).updateCardAdmin(cardId, dto);
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/admin/cards/delete/{id} -204 No Content")
    void deleteCard() throws Exception {
        Long cardId = 10L;

        mockMvc.perform(delete("/api/admin/cards/delete/{id}", cardId))
                .andExpect(status().isNoContent());

        verify(cardService, times(1)).deleteCardAdmin(cardId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /api/admin/cards/block/{id} - 200 OK")
    void blockCard() throws Exception {
        Long cardId = 10L;

        CardResponse resp = sample();
        resp.setStatus(Status.BLOCKED);

        when(cardService.blockCardAdmin(cardId)).thenReturn(resp);

        mockMvc.perform(patch("/api/admin/cards/block/{id}", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("BLOCKED"));

        verify(cardService, times(1)).blockCardAdmin(cardId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PATCH /api/admin/cards/activate/{id} - 200 OK")
    void activateCard() throws Exception {
        Long cardId = 10L;

        CardResponse resp = sample();
        resp.setStatus(Status.ACTIVE);

        when(cardService.activateCardAdmin(cardId)).thenReturn(resp);

        mockMvc.perform(patch("/api/admin/cards/activate/{id}", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(cardService, times(1)).activateCardAdmin(cardId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/cards/{id} - 200 OK")
    void getCardById() throws Exception {
        Long cardId = 10L;
        when(cardService.getByIdAdmin(cardId)).thenReturn(sample());

        mockMvc.perform(get("/api/admin/cards/{id}", cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.ownerName").value("Nikita"))
                .andExpect(jsonPath("$.currency").value("KZT")
                )
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.panMasked").value("**** **** **** 1234"));


        verify(cardService, times(1)).getByIdAdmin(cardId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/admin/cards - 200 OK (page)")
    void listCards() throws Exception {
        CardResponse r1 = sample();
        CardResponse r2 = sample();
        r2.setId(11L);

        Pageable pageable = PageRequest.of(0, 2, Sort.unsorted());
        Page<CardResponse> page = new PageImpl<>(List.of(r1, r2), pageable, 2);

        when(cardService.showAllCardsAdmin(any(Pageable.class), eq(10L), eq(Status.ACTIVE), eq("1234")))
                .thenReturn(page);

        mockMvc.perform(get("/api/admin/cards")
                        .queryParam("page", "0")
                        .queryParam("size", "2")
                        .queryParam("userId", "10")
                        .queryParam("status", "ACTIVE")
                        .queryParam("last4", "1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[1].id").value(11))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.number").value(0));

        verify(cardService, times(1))
                .showAllCardsAdmin(any(Pageable.class), eq(10L), eq(Status.ACTIVE), eq("1234"));
    }
}
