package com.example.bankcards.controller;


import com.example.bankcards.dto.CardDTO.CardResponse;
import com.example.bankcards.dto.CardDTO.CreateCardRequest;
import com.example.bankcards.dto.CardDTO.UpdateCardRequest;
import com.example.bankcards.dto.userdto.AuthUser;
import com.example.bankcards.entity.enums.Status;
import com.example.bankcards.service.Impl.CardServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/** Класс контроллера отвечающий за управление картами пользователями с ролью ADMIN */
@RestController
@RequestMapping("/api/admin/cards")
@AllArgsConstructor
@Validated
@Tag(name="Admin Card Controller", description = "Controller for admin card management")
public class AdminCardController {

    private CardServiceImpl cardService;


    /** Метод контроллера реализующий создание карты админом
     * @param request Принимает Json тело запроса на создание карты CreateCardRequest
     * @return Возвращает ResponseEntity с телом CardResponse и кодом 201
     * @see CreateCardRequest
     * @see CardResponse
     * @see CardServiceImpl#createCardAdmin(CreateCardRequest) */
    @Operation(summary = "Create card")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<CardResponse> createCard(@RequestBody @Valid CreateCardRequest request){
        CardResponse response= cardService.createCardAdmin(request);

        return ResponseEntity.created(URI.create("/api/cards/"+response.getId()))
                .body(response);
    }


    /** Метод контроллера реализующий обновление карты админом
     * @param request Принимает Json тело запроса на создание карты CreateCardRequest
     * @param cardId Принимает id карты для обновления
     * @return Возвращает ResponseEntity с телом CardResponse и кодом 200
     * @see CreateCardRequest
     * @see CardResponse
     * @see CardServiceImpl#updateCardAdmin(Long, UpdateCardRequest) */
    @Operation(summary = "Update card")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/update/{cardId}")
    public ResponseEntity<CardResponse> updateCard(@PathVariable Long cardId, @RequestBody @Valid UpdateCardRequest request){

        CardResponse response= cardService.updateCardAdmin(cardId, request);

        return ResponseEntity.ok(response);

    }

    /** Метод контроллера реализующий удаление карты админом
     * @param cardId Принимает id карты для обновления
     * @return Возвращает ResponseEntity с пустым телом*/
    @Operation(summary = "Delete card")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/delete/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId){

        cardService.deleteCardAdmin(cardId);

        return ResponseEntity.noContent().build();
    }


    /** Метод блокирует карту пользователя
     * @param cardId Принимает id карты которую нужно заблокировать
     * @return Возвращает ResponseEntity с телом CardResponse карты
     * @see CardServiceImpl#blockCardAdmin(Long)
     * @see CardResponse*/
    @Operation(summary = "Block card")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/block/{cardId}")
    public ResponseEntity<CardResponse> blockCard(@PathVariable Long cardId){

        CardResponse response= cardService.blockCardAdmin(cardId);

        return ResponseEntity.ok(response);
    }

    /** Метод активирует карту пользователя
     * @param cardId Принимает id карты которую нужно активирует
     * @return Возвращает ResponseEntity с телом CardResponse карты
     * @see CardServiceImpl#activateCardAdmin(Long)
     * @see CardResponse*/
    @Operation(summary = "Activate card")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PatchMapping("/activate/{cardId}")
    public ResponseEntity<CardResponse> activateCard(@PathVariable Long cardId){

        CardResponse response= cardService.activateCardAdmin(cardId);

        return ResponseEntity.ok(response);
    }


    /** Метод возвращающий все карты конкретного пользователя
     * @param pageable Принимает настройки вывода страниц
     * @param userId Принимает необязательные параметр id пользователя карты которого нужно получить
     * @param status Принимает необязательный параметр статуса карт которые нужно получить
     * @param last4 Принимает необязательный параметр из последних 4х цифр PAN кода карты, для карт которые нужно получить
     * @return Возвращает список полученых карт и код 200

     * @see CardResponse*/
    @Operation(summary = "Get all card with parameters")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping()
    public ResponseEntity<Page<CardResponse>> list(Pageable pageable, @RequestParam(required = false) Long userId,
                                                  @RequestParam(required = false) Status status,
                                                  @RequestParam(required = false) String last4 ){



        return ResponseEntity.ok(
                cardService.showAllCardsAdmin(pageable,userId,status,last4));

    }

    /** Метод возвращает карту пользователя по id карты
     * @param cardId Принимает id карты
     * @return Возвращает ResponseEntity с телом CardResponse

     * @see CardResponse
     * @see CardServiceImpl#getByIdAdmin(Long) */
    @Operation(summary = "Get card by id")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponse> getCard(@PathVariable Long cardId){

        return ResponseEntity.ok(cardService.getByIdAdmin(cardId));

    }



}
