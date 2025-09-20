package com.example.bankcards.service;

import com.example.bankcards.dto.TransferDTO.CreateTransferRequest;
import com.example.bankcards.dto.TransferDTO.TransferResponse;
import org.springframework.stereotype.Service;


public interface TransferService {

    TransferResponse executeTransfer(Long userId, CreateTransferRequest dto);
}
