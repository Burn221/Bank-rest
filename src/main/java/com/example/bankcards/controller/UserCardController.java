package com.example.bankcards.controller;


import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.CardDTO.CardResponse;
import com.example.bankcards.dto.CardDTO.CreateCardRequest;
import com.example.bankcards.dto.userdto.AuthUser;
import com.example.bankcards.service.Impl.CardServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@AllArgsConstructor
@RestController
@Validated
@RequestMapping("/api/me/cards")
@Tag(name="User Card Controller", description = "Controller for user card management")
public class UserCardController {

    private CardServiceImpl cardService;


    @Operation(summary = "Create card by user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public ResponseEntity<CardResponse> createMyCard(@AuthenticationPrincipal AuthUser me,
                                                       @RequestBody @Valid CreateCardRequest request){

        CardResponse response= cardService.createCardUser(me.id(),request);

        return ResponseEntity.created(URI.create("/api/me/cards/"+response.getId())).body(response);
    }


    @Operation(summary = "Get all current user cards")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public ResponseEntity<Page<CardResponse>> showMyCards(@AuthenticationPrincipal AuthUser me,
                                                          Pageable pageable){


        return ResponseEntity.ok(cardService.showMyCardsUser(me.id(), pageable));
    }


    @Operation(summary = "Get current user card by id")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getMyCard(@AuthenticationPrincipal AuthUser me,
                                                  @PathVariable Long cardId){


        return ResponseEntity.ok(cardService.getMyCardUser(cardId, me.id()));
    }


    @Operation(summary = "Get balance of current user card")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/balance/{cardId}")
    public ResponseEntity<BalanceResponse> getMyBalance(@AuthenticationPrincipal AuthUser me,
                                                        @PathVariable Long cardId){

        return ResponseEntity.ok(cardService.showMyBalanceUser(me.id(), cardId));
    }


    @Operation(summary = "Block current user card")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/block/{cardId}")
    public ResponseEntity<CardResponse> blockMyCard(@AuthenticationPrincipal AuthUser me,
                                                    @PathVariable Long cardId){

        return ResponseEntity.ok(cardService.blockRequestUser(me.id(), cardId));
    }




}
