package com.example.bankcards.controller;


import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.CardDTO.BlockCardRequest;
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

/** Класс контроллер для управления картами пользователем */
@AllArgsConstructor
@RestController
@Validated
@RequestMapping("/api/me/cards")
@Tag(name="User Card Controller", description = "Controller for user card management")
public class UserCardController {

    private CardServiceImpl cardService;


    /** Метод контроллера реализующий создание карты пользователем
     * @param me Принимает Dto AuthUser содержащее данные о пользователе из JWT токена, его id, username, role и т.д
     * @param request Принимает Json тело запроса на создание карты CreateCardRequest
     * @return Возвращает ResponseEntity с телом CardResponse и кодом 201
     * @see CreateCardRequest
     * @see AuthUser
     * @see CardResponse*/
    @Operation(summary = "Create card by user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public ResponseEntity<CardResponse> createMyCard(@AuthenticationPrincipal AuthUser me,
                                                       @RequestBody @Valid CreateCardRequest request){

        CardResponse response= cardService.createCardUser(me.id(),request);

        return ResponseEntity.created(URI.create("/api/me/cards/"+response.getId())).body(response);
    }

    /** Метод возвращающий все карты конкретного пользователя
     * @param me Принимает Dto AuthUser содержащее данные о пользователе из JWT токена, его id, username, role и т.д
     * @param pageable Принимает настройки вывода страниц
     * @return Возвращает список полученых карт пользователя и код 200
     * @see AuthUser
     * @see CardResponse*/
    @Operation(summary = "Get all current user cards")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public ResponseEntity<Page<CardResponse>> showMyCards(@AuthenticationPrincipal AuthUser me,
                                                          Pageable pageable){


        return ResponseEntity.ok(cardService.showMyCardsUser(me.id(), pageable));
    }

    /** Метод возвращает карту текущего пользователя по id карты
     * @param me Принимает Dto AuthUser содержащее данные о пользователе из JWT токена, его id, username, role и т.д
     * @param cardId Принимает id карты
     * @return Возвращает ResponseEntity с телом CardResponse
     * @see AuthUser
     * @see CardResponse*/
    @Operation(summary = "Get current user card by id")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getMyCard(@AuthenticationPrincipal AuthUser me,
                                                  @PathVariable Long cardId){


        return ResponseEntity.ok(cardService.getMyCardUser(cardId, me.id()));
    }

    /** Метод получающий баланс текущего пользователя
     * @param me Принимает Dto AuthUser содержащее данные о пользователе из JWT токена, его id, username, role и т.д
     * @param cardId Принимает id карты с которой нужно получить баланс
     * @return Возвращает ResponseEntity с телом BalanceResponse
     * @see AuthUser
     * @see BalanceResponse
     */
    @Operation(summary = "Get balance of current user card")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/balance/{cardId}")
    public ResponseEntity<BalanceResponse> getMyBalance(@AuthenticationPrincipal AuthUser me,
                                                        @PathVariable Long cardId){

        return ResponseEntity.ok(cardService.showMyBalanceUser(me.id(), cardId));
    }


    /** Метод запрашивает блокировку карты текущего пользователя
     * @param me Принимает Dto AuthUser содержащее данные о пользователе из JWT токена, его id, username, role и т.д
     * @param cardId Принимает id карты которую нужно заблокировать
     * @return Возвращает ResponseEntity с телом CardResponse карты
     * @see AuthUser
     * @see CardResponse*/
    @Operation(summary = "Block current user card")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/block/{cardId}")
    public ResponseEntity<CardResponse> blockMyCard(@AuthenticationPrincipal AuthUser me,
                                                    @PathVariable Long cardId, @RequestBody BlockCardRequest request){

        return ResponseEntity.ok(cardService.blockRequestUser(me.id(), cardId, request));
    }




}
