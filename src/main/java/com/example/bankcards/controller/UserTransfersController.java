package com.example.bankcards.controller;

import com.example.bankcards.dto.TransferDTO.CreateTransferRequest;
import com.example.bankcards.dto.TransferDTO.TransferResponse;
import com.example.bankcards.dto.userdto.AuthUser;
import com.example.bankcards.service.Impl.TransferServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@AllArgsConstructor
@RestController
@Validated
@RequestMapping("/api/me/transfers")
@Tag(name="User Transfer Controller", description = "Controller for user transfer management")
public class UserTransfersController {
    private TransferServiceImpl transferService;


    @Operation(summary = "Execute transfer between current user cards", description = "Transfer must be between cards of the same user")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<TransferResponse> executeTransfer(@AuthenticationPrincipal AuthUser me,
                                                            @RequestBody @Valid CreateTransferRequest request){


        TransferResponse response= transferService.executeTransfer(me.id(), request);

        return ResponseEntity.created(URI.create("/api/me/transfers/"+response.id())).body(response);

    }


}
