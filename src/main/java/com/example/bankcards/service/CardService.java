package com.example.bankcards.service;

import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.CardDTO.BlockCardRequest;
import com.example.bankcards.dto.CardDTO.CardResponse;
import com.example.bankcards.dto.CardDTO.CreateCardRequest;
import com.example.bankcards.dto.CardDTO.UpdateCardRequest;
import com.example.bankcards.entity.enums.Status;
import io.micrometer.common.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CardService {

    CardResponse createCardAdmin(CreateCardRequest dto);

    CardResponse updateCardAdmin( Long cardId, UpdateCardRequest dto);

    void deleteCardAdmin(Long cardId);

    CardResponse blockCardAdmin(Long cardId);

    CardResponse activateCardAdmin(Long cardId);

    Page<CardResponse> showAllCardsAdmin(Pageable pageable, @Nullable Long userId
            , @Nullable Status status, @Nullable String last4 );


    CardResponse getByIdAdmin(Long cardId);

    CardResponse createCardUser(Long userId, CreateCardRequest dto);

    Page<CardResponse> showMyCardsUser(Long userId, Pageable pageable);

    CardResponse getMyCardUser(Long userId, Long cardId);

    BalanceResponse showMyBalanceUser(Long userId, Long cardId);

    CardResponse blockRequestUser(Long userId, Long cardId, BlockCardRequest request);






}
