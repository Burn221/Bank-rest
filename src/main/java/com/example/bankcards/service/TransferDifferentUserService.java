package com.example.bankcards.service;

import com.example.bankcards.dto.TransferDTO.TransferDifferentUsersResponse;

public interface TransferDifferentUserService {

    //todo
    TransferDifferentUsersResponse executeTransferToDifferentUser(Long userId,Long fromCardId, String toPan );
}
