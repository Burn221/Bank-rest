package com.example.bankcards.service.Impl;

import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.enums.TransferStatus;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.service.TransferStatusService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TransferStatusServiceImpl implements TransferStatusService {

    private CardRepository cardRepository;
    private TransferRepository transferRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(Long fromId, Long toId, long amountMinor, String currency) {
        Transfer t = new Transfer();
        t.setFromCard(cardRepository.getReferenceById(fromId));
        t.setToCard(cardRepository.getReferenceById(toId));
        t.setAmountMinor(amountMinor);
        t.setCurrency(currency);
        t.setTransferStatus(TransferStatus.FAILED);
        t.setCreatedAt(LocalDateTime.now());
        transferRepository.save(t);
    }
}
