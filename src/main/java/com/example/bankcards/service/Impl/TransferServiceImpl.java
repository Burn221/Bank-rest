package com.example.bankcards.service.Impl;

import com.example.bankcards.dto.TransferDTO.CreateTransferRequest;
import com.example.bankcards.dto.TransferDTO.TransferMapper;
import com.example.bankcards.dto.TransferDTO.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.enums.Status;
import com.example.bankcards.entity.enums.TransferStatus;
import com.example.bankcards.exception.exceptions.ForbiddenTransactionException;
import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.service.TransferService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


/** Класс сервиса для работы с переводами */
@Service
@AllArgsConstructor


public class TransferServiceImpl implements TransferService {

    private TransferRepository transferRepository;
    private CardRepository cardRepository;
    private TransferMapper mapper;

    /** Метод осуществляющий перевод между картами пользователями
     * @param userId Принимает id пользователя
     * @param dto Принимает CreateTransferRequest dto который содержит поля: fromCard, toCard, amountMinor, currency
     * @throws ForbiddenTransactionException Если транзакция не соответсвует условиям или запрещена
     * @throws NotFoundException Если карта не найден
     * @return Возвращает TransferResponse с полями:
     *                     <ul>
     *                        <li>id - идентификатор созданного перевода</li>
     *                        <li>fromCardId - id карты с которой производится перевод</li>
     *                        <li>toCardId - id карты на которую переводят</li>
     *                        <li>amountMinor - Сумма перевода</li>
     *                        <li>currency - Валюта перевода</li>
     *                        <li>transferStatus - Статус перевода: SUCCESS/FAILED/PENDING</li>
     *                         <li>createdAt - дата и время создания</li>
     *                   </ul>
     * @see CardRepository#findForUpdate(Long)  */
    @Override @Transactional
    public TransferResponse executeTransfer(Long userId, CreateTransferRequest dto) {
        if(dto.fromCard().equals(dto.toCard())) throw new ForbiddenTransactionException("Transfer to the same card are forbidden");
        if(dto.amountMinor()==null || dto.amountMinor()<=0) throw new ForbiddenTransactionException("Transfer amount must be > 0");


        Card fromCard= cardRepository.findForUpdate(dto.fromCard()).orElseThrow(()-> new NotFoundException("Card not found"));
        Card toCard= cardRepository.findForUpdate(dto.toCard()).orElseThrow(()-> new NotFoundException("Card not found"));


        if(!fromCard.getCurrency().equals(toCard.getCurrency())) throw new ForbiddenTransactionException("Transfer to the same card are forbidden");
        if(!fromCard.getStatus().equals(Status.ACTIVE) || !toCard.getStatus().equals(Status.ACTIVE) ) throw new ForbiddenTransactionException("Card is not active");
        if(!fromCard.getUser().getId().equals(toCard.getUser().getId()) || !fromCard.getUser().getId().equals(userId) || !toCard.getUser().getId().equals(userId)) throw new ForbiddenTransactionException("Transaction must be between same user card");
        if(fromCard.getBalanceMinor()<dto.amountMinor()) throw new ForbiddenTransactionException("Your card has not enough money fr transaction");

        fromCard.setBalanceMinor(fromCard.getBalanceMinor()-dto.amountMinor());
        toCard.setBalanceMinor(toCard.getBalanceMinor()+dto.amountMinor());

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        Transfer transfer= mapper.toEntity(fromCard,toCard,dto);
        transfer.setCurrency(fromCard.getCurrency());
        transfer.setTransferStatus(TransferStatus.SUCCESS);
        transfer.setCreatedAt(LocalDateTime.now());


        return mapper.toResponse(transferRepository.save(transfer));


    }
}
