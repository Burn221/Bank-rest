package com.example.bankcards.controller;


import com.example.bankcards.dto.CardDTO.CardResponse;
import com.example.bankcards.dto.CardDTO.CreateCardRequest;
import com.example.bankcards.dto.CardDTO.UpdateCardRequest;
import com.example.bankcards.entity.enums.Status;
import com.example.bankcards.service.Impl.CardServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/admin/cards")
@AllArgsConstructor
@Validated
@Tag(name="Admin Card Controller", description = "Controller for admin card management")
public class AdminCardController {

    private CardServiceImpl cardService;


    @Operation(summary = "Create card")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<CardResponse> createCard(@RequestBody @Valid CreateCardRequest request){
        CardResponse response= cardService.createCardAdmin(request);

        return ResponseEntity.created(URI.create("/api/cards/"+response.getId()))
                .body(response);
    }

    @Operation(summary = "Update card")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/update/{cardId}")
    public ResponseEntity<CardResponse> updateCard(@PathVariable Long cardId, @RequestBody @Valid UpdateCardRequest request){

        CardResponse response= cardService.updateCardAdmin(cardId, request);

        return ResponseEntity.ok(response);

    }

    @Operation(summary = "Delete card")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/delete/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId){

        cardService.deleteCardAdmin(cardId);

        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Block card")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/block/{cardId}")
    public ResponseEntity<CardResponse> blockCard(@PathVariable Long cardId){

        CardResponse response= cardService.blockCardAdmin(cardId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activate card")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/activate/{cardId}")
    public ResponseEntity<CardResponse> activateCard(@PathVariable Long cardId){

        CardResponse response= cardService.activateCardAdmin(cardId);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Get all card with parameters")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping()
    public ResponseEntity<?> list(Pageable pageable, @RequestParam(required = false) Long userId,
                                  @RequestParam(required = false) Status status,
                                  @RequestParam(required = false) String last4 ){

        return ResponseEntity.ok(
                cardService.showAllCardsAdmin(pageable,userId,status,last4));

    }

    @Operation(summary = "Get card by id")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCard(@PathVariable Long cardId){

        return ResponseEntity.ok(cardService.getByIdAdmin(cardId));

    }



}
