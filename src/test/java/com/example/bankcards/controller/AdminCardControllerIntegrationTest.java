package com.example.bankcards.controller;
import com.example.bankcards.BankRestApplication;
import com.example.bankcards.Configuration.TestBeans;
import com.example.bankcards.dto.CardDTO.CardResponse;
import com.example.bankcards.entity.enums.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

@SpringBootTest(classes = {BankRestApplication.class, TestBeans.class})
@AutoConfigureMockMvc
public class AdminCardControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    private CardResponse sample() {
        CardResponse r = new CardResponse();
        r.setId(100L);
        r.setUserId(10L);
        r.setPanMasked("**** **** **** 1234");
        r.setStatus(Status.ACTIVE);
        r.setCurrency("KZT");
        r.setCreatedAt(LocalDateTime.now());
        return r;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/admin/cards/create should return 201 Created")
    void createCard() throws Exception{
        this.mockMvc.perform(post("/api/admin/cards/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
        {
          "userId": 1,
          "ownerName":"Nikita" ,
          "currency":"KZT"
        }
"""))
                .andExpectAll(
                        status().isCreated(),
                        MockMvcResultMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.id").exists(),
                        MockMvcResultMatchers.jsonPath("$.ownerName").value("Nikita"),
                        MockMvcResultMatchers.jsonPath("$.currency").value("KZT")


                );
    }



}
