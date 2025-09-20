package com.example.bankcards.service;

import com.example.bankcards.dto.CardDTO.CardResponse;
import com.example.bankcards.dto.CardDTO.CreateCardRequest;
import com.example.bankcards.dto.CardDTO.UpdateCardRequest;

public interface CardService {

    CardResponse createCard(CreateCardRequest dto);

    CardResponse updateCard(UpdateCardRequest dto);




}
