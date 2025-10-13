package com.example.bankcards.service;

import com.example.bankcards.dto.TransferDTO.CreateTransferDifferentUserRequest;
import com.example.bankcards.dto.TransferDTO.CreateTransferRequest;
import com.example.bankcards.dto.TransferDTO.TransferDifferentUsersResponse;
import com.example.bankcards.dto.TransferDTO.TransferResponse;



public interface TransferService {

    TransferResponse executeTransfer(Long userId, CreateTransferRequest dto);

    TransferDifferentUsersResponse executeTransferToDifferentUser(Long userId, CreateTransferDifferentUserRequest request);
}
