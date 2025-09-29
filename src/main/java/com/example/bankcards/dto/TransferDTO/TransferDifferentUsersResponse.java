package com.example.bankcards.dto.TransferDTO;

//todo
public record TransferDifferentUsersResponse(
        Long id,
        Long fromCardId,
        String fromOwnerName,
        String fromPanMasked,
        Long amount,
        Long toCardId,
        String toOwnerName,
        String toPanMasked


) {
}
