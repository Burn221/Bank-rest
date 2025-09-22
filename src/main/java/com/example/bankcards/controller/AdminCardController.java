package com.example.bankcards.controller;


import com.example.bankcards.dto.CardDTO.CardResponse;
import com.example.bankcards.dto.CardDTO.CreateCardRequest;
import com.example.bankcards.dto.CardDTO.UpdateCardRequest;
import com.example.bankcards.entity.enums.Status;
import com.example.bankcards.service.Impl.CardServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/admin/cards")
@AllArgsConstructor
@Validated
public class AdminCardController {

    private CardServiceImpl cardService;

    @PostMapping("/create")
    public ResponseEntity<CardResponse> createCard(@RequestBody @Valid CreateCardRequest request){
        CardResponse response= cardService.createCardAdmin(request);

        return ResponseEntity.created(URI.create("/api/cards/"+response.getId()))
                .body(response);
    }

    @PutMapping("/update/{cardId}")
    public ResponseEntity<CardResponse> updateCard(@PathVariable Long cardId, @RequestBody @Valid UpdateCardRequest request){

        CardResponse response= cardService.updateCardAdmin(cardId, request);

        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/delete/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId){

        cardService.deleteCardAdmin(cardId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/block/{cardId}")
    public ResponseEntity<CardResponse> blockCard(@PathVariable Long cardId){

        CardResponse response= cardService.blockCardAdmin(cardId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/activate/{cardId}")
    public ResponseEntity<CardResponse> activateCard(@PathVariable Long cardId){

        CardResponse response= cardService.activateCardAdmin(cardId);

        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<?> list(Pageable pageable, @RequestParam(required = false) Long userId,
                                  @RequestParam(required = false) Status status,
                                  @RequestParam(required = false) String last4 ){

        return ResponseEntity.ok(
                cardService.showAllCardsAdmin(pageable,userId,status,last4));

    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCard(@PathVariable Long cardId){

        return ResponseEntity.ok(cardService.getByIdAdmin(cardId));

    }



}
