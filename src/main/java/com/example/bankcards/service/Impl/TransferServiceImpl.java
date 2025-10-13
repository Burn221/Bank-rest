package com.example.bankcards.service.Impl;

import com.example.bankcards.dto.TransferDTO.*;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Status;
import com.example.bankcards.entity.enums.TransferStatus;
import com.example.bankcards.exception.exceptions.DisabledException;
import com.example.bankcards.exception.exceptions.FailedTransactionException;
import com.example.bankcards.exception.exceptions.ForbiddenTransactionException;
import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.util.AesGcm;
import lombok.AllArgsConstructor;
import org.springframework.dao.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


/**
 * Класс сервиса для работы с переводами
 */
@Service
@AllArgsConstructor


public class TransferServiceImpl implements TransferService {

    private TransferRepository transferRepository;
    private CardRepository cardRepository;
    private TransferMapper mapper;
    private TransferStatusServiceImpl transferStatusService;



    /**
     * Метод осуществляющий перевод между картами пользователями
     *
     * @param userId Принимает id пользователя
     * @param dto    Принимает CreateTransferRequest dto который содержит поля: fromCard, toCard, amountMinor, currency
     * @return Возвращает TransferResponse с полями:
     *   <ul>
     *      <li>id - идентификатор созданного перевода</li>
     *      <li>fromCardId - id карты с которой производится перевод</li>
     *      <li>toCardId - id карты на которую переводят</li>
     *      <li>amountMinor - Сумма перевода</li>
     *      <li>currency - Валюта перевода</li>
     *      <li>transferStatus - Статус перевода: SUCCESS/FAILED/PENDING</li>
     *       <li>createdAt - дата и время создания</li>
     * </ul>
     * @throws ForbiddenTransactionException Если транзакция не соответсвует условиям или запрещена
     * @throws NotFoundException             Если карта не найден
     * @see CardRepository#findForUpdate(Long)
     */
    @Override
    @Transactional
    public TransferResponse executeTransfer(Long userId, CreateTransferRequest dto) {
        try {
            if (dto.fromCard().equals(dto.toCard()))
                throw new ForbiddenTransactionException("Transfers to the same card are forbidden");
            if (dto.amountMinor() == null || dto.amountMinor() <= 0)
                throw new ForbiddenTransactionException("Transfer amount must be > 0");


            Card fromCard = cardRepository.findForUpdate(dto.fromCard()).orElseThrow(() -> new NotFoundException("Card not found"));
            Card toCard = cardRepository.findForUpdate(dto.toCard()).orElseThrow(() -> new NotFoundException("Card not found"));


            if (!fromCard.getCurrency().equals(toCard.getCurrency()))
                throw new ForbiddenTransactionException("Currencies mismatch");
            if (!fromCard.getStatus().equals(Status.ACTIVE) || !toCard.getStatus().equals(Status.ACTIVE))
                throw new FailedTransactionException("Card is not active");
            if (!fromCard.getUser().getId().equals(toCard.getUser().getId()) || !fromCard.getUser().getId().equals(userId) || !toCard.getUser().getId().equals(userId))
                throw new ForbiddenTransactionException("Transaction must be between same user card");
            if (fromCard.getBalanceMinor() < dto.amountMinor())
                throw new ForbiddenTransactionException("Your card has not enough money for transaction");

            fromCard.setBalanceMinor(fromCard.getBalanceMinor() - dto.amountMinor());
            toCard.setBalanceMinor(toCard.getBalanceMinor() + dto.amountMinor());

            cardRepository.save(fromCard);
            cardRepository.save(toCard);

            Transfer transfer = mapper.toEntity(fromCard, toCard, dto);
            transfer.setCurrency(fromCard.getCurrency());
            transfer.setTransferStatus(TransferStatus.SUCCESS);
            transfer.setCreatedAt(LocalDateTime.now());


            return mapper.toResponse(transferRepository.save(transfer));
        } catch ( FailedTransactionException  | CannotAcquireLockException
                | OptimisticLockingFailureException | QueryTimeoutException
                | RecoverableDataAccessException e) {
            transferStatusService.markFailed(dto.fromCard(), dto.toCard(), dto.amountMinor(), dto.currency());
            throw e;

        }




    }

    
    @Override
    @Transactional
    public TransferDifferentUsersResponse executeTransferToDifferentUser(Long userId, CreateTransferDifferentUserRequest request) {


        try {
            String panEncrypted = AesGcm.encryptToBase64(request.getToPan());
            Card toCard = cardRepository.findForUpdateByPan(panEncrypted)
                    .orElseThrow(() -> new NotFoundException("Card not found"));

            Card fromCard = cardRepository.findForUpdate(request.getFromCardId())
                    .orElseThrow(() -> new NotFoundException("Card not found"));


            if (fromCard.getUser().getId().equals(toCard.getUser().getId()))
                throw new ForbiddenTransactionException("Transfer must be between different users");
            if (fromCard.getId().equals(toCard.getId()))
                throw new ForbiddenTransactionException("Transfers to the same card are forbidden");
            if (request.getAmount() <= 0) throw new ForbiddenTransactionException("Transfer amount must be > 0");
            if (!fromCard.getCurrency().equals(toCard.getCurrency()))
                throw new FailedTransactionException("Currencies mismatch");
            if (!fromCard.getStatus().equals(Status.ACTIVE) || !toCard.getStatus().equals(Status.ACTIVE))
                throw new FailedTransactionException("Card is not active");


            long amount = request.getAmount();
            long amountWithCommission = (amount * 101L + 99L) / 100L;

            if (fromCard.getBalanceMinor() < amountWithCommission)
                throw new ForbiddenTransactionException("Your card has not enough money for transaction");

            fromCard.setBalanceMinor(fromCard.getBalanceMinor() - amountWithCommission);
            toCard.setBalanceMinor(toCard.getBalanceMinor() + request.getAmount());

            cardRepository.save(fromCard);
            cardRepository.save(toCard);

            Transfer transfer = new Transfer();
            transfer.setFromCard(fromCard);
            transfer.setToCard(toCard);
            transfer.setAmountMinor(request.getAmount());
            transfer.setCurrency(request.getCurrency());
            transfer.setCreatedAt(LocalDateTime.now());
            transfer.setTransferStatus(TransferStatus.SUCCESS);
            transferRepository.save(transfer);

            TransferDifferentUsersResponse response = new TransferDifferentUsersResponse();
            response.setFromCardId(fromCard.getId());
            response.setToCardId(toCard.getId());
            response.setAmount(request.getAmount());
            response.setFromOwnerName(fromCard.getOwnerName());
            response.setToOwnerName(toCard.getOwnerName());
            response.setFromPanMasked(fromCard.getPanLast4());
            response.setToPanMasked(toCard.getPanLast4());


            return response;
        } catch ( FailedTransactionException  | CannotAcquireLockException
                 | OptimisticLockingFailureException | QueryTimeoutException
                 | RecoverableDataAccessException e) {
            Long toId= cardRepository.getIdByPan(AesGcm.encryptToBase64(request.getToPan()));
            transferStatusService.markFailed(request.getFromCardId(), toId, request.getAmount(), request.getCurrency());
            throw e;

        }


    }


}
