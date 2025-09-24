package com.example.bankcards.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.bankcards.dto.TransferDTO.CreateTransferRequest;
import com.example.bankcards.dto.TransferDTO.TransferMapper;
import com.example.bankcards.dto.TransferDTO.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Status;
import com.example.bankcards.entity.enums.TransferStatus;
import com.example.bankcards.exception.exceptions.ForbiddenTransactionException;
import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.service.Impl.TransferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransferServiceImplTest {

    @Mock private TransferRepository transferRepository;
    @Mock private CardRepository cardRepository;
    @Mock private TransferMapper mapper;

    @InjectMocks private TransferServiceImpl service;

    private User owner;
    private Card from;
    private Card to;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(10L);

        from = new Card();
        from.setId(1L);
        from.setUser(owner);
        from.setStatus(Status.ACTIVE);
        from.setCurrency("KZT");
        from.setBalanceMinor(1_000_00L);
        from.setCreatedAt(LocalDateTime.now());

        to = new Card();
        to.setId(2L);
        to.setUser(owner);
        to.setStatus(Status.ACTIVE);
        to.setCurrency("KZT");
        to.setBalanceMinor(100_00L);
        to.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("OK - собственные активные карты, одна валюта, хватает средств")
    void executeTransfer_success() {
        CreateTransferRequest dto = new CreateTransferRequest(1L, 2L, 300_00L, "KZT");

        when(cardRepository.findForUpdate(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findForUpdate(2L)).thenReturn(Optional.of(to));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        Transfer tr = new Transfer();
        tr.setId(10L);
        tr.setFromCard(from);
        tr.setToCard(to);
        tr.setAmountMinor(dto.amountMinor());
        tr.setCurrency("KZT");
        tr.setTransferStatus(TransferStatus.SUCCESS);
        tr.setCreatedAt(LocalDateTime.now());

        when(mapper.toEntity(from, to, dto)).thenReturn(tr);
        when(transferRepository.save(any(Transfer.class))).thenAnswer(inv -> inv.getArgument(0));

        TransferResponse mapped = new TransferResponse(10L,1L,2L,300_00L,"KZT"
        ,TransferStatus.SUCCESS,LocalDateTime.now());

        when(mapper.toResponse(any(Transfer.class))).thenReturn(mapped);

        TransferResponse resp = service.executeTransfer(10L, dto);

        assertThat(resp.transferStatus()).isEqualTo(TransferStatus.SUCCESS);
        assertThat(from.getBalanceMinor()).isEqualTo(700_00L);
        assertThat(to.getBalanceMinor()).isEqualTo(400_00L);

        verify(cardRepository, times(2)).findForUpdate(anyLong());
        verify(cardRepository, times(2)).save(any(Card.class));
        verify(transferRepository).save(any(Transfer.class));
        verify(mapper).toEntity(from, to, dto);
        verify(mapper).toResponse(any(Transfer.class));
    }


    @Test
    @DisplayName("запрет перевода на ту же карту")
    void executeTransfer_sameCard_forbidden() {
        CreateTransferRequest dto = new CreateTransferRequest(1L, 1L, 100L, "KZT");
        assertThatThrownBy(() -> service.executeTransfer(10L, dto))
                .isInstanceOf(ForbiddenTransactionException.class)
                .hasMessageContaining("same card");
        verifyNoInteractions(cardRepository, transferRepository, mapper);
    }

    @Test
    @DisplayName("сумма <= 0 - Forbidden")
    void executeTransfer_nonPositiveAmount_forbidden() {
        CreateTransferRequest dto = new CreateTransferRequest(1L, 2L, 0L, "KZT");
        assertThatThrownBy(() -> service.executeTransfer(10L, dto))
                .isInstanceOf(ForbiddenTransactionException.class)
                .hasMessageContaining("must be > 0");
        verifyNoInteractions(cardRepository, transferRepository, mapper);
    }

    @Test
    @DisplayName("fromCard не найден - NotFound")
    void executeTransfer_fromNotFound() {
        CreateTransferRequest dto = new CreateTransferRequest(1L, 2L, 100L, "KZT");
        when(cardRepository.findForUpdate(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.executeTransfer(10L, dto))
                .isInstanceOf(NotFoundException.class);
    }


    @Test
    @DisplayName("toCard не найден - NotFound")
    void executeTransfer_toNotFound() {
        CreateTransferRequest dto = new CreateTransferRequest(1L, 2L, 100L, "KZT");
        when(cardRepository.findForUpdate(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findForUpdate(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.executeTransfer(10L, dto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("валюты не совпадают - Forbidden")
    void executeTransfer_currencyMismatch_forbidden() {
        to.setCurrency("USD");
        CreateTransferRequest dto = new CreateTransferRequest(1L, 2L, 100L, "KZT");
        when(cardRepository.findForUpdate(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findForUpdate(2L)).thenReturn(Optional.of(to));

        assertThatThrownBy(() -> service.executeTransfer(10L, dto))
                .isInstanceOf(ForbiddenTransactionException.class)
                .hasMessageContaining("same card");
    }

    @Test
    @DisplayName("любая карта не ACTIVE - Forbidden")
    void executeTransfer_notActive_forbidden() {
        from.setStatus(Status.BLOCKED);
        CreateTransferRequest dto = new CreateTransferRequest(1L, 2L, 100L, "KZT");
        when(cardRepository.findForUpdate(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findForUpdate(2L)).thenReturn(Optional.of(to));

        assertThatThrownBy(() -> service.executeTransfer(10L, dto))
                .isInstanceOf(ForbiddenTransactionException.class)
                .hasMessageContaining("not active");
    }


    @Test
    @DisplayName("карты принадлежат разным пользователям - Forbidden")
    void executeTransfer_differentOwners_forbidden() {
        User other = new User();
        other.setId(999L);
        to.setUser(other);

        CreateTransferRequest dto = new CreateTransferRequest(1L, 2L, 100L, "KZT");
        when(cardRepository.findForUpdate(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findForUpdate(2L)).thenReturn(Optional.of(to));

        assertThatThrownBy(() -> service.executeTransfer(10L, dto))
                .isInstanceOf(ForbiddenTransactionException.class)
                .hasMessageContaining("same user card");
    }

    @Test
    @DisplayName("недостаточно средств - Forbidden")
    void executeTransfer_notEnoughFunds_forbidden() {
        CreateTransferRequest dto = new CreateTransferRequest(1L, 2L, 2_000_00L, "KZT");
        when(cardRepository.findForUpdate(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findForUpdate(2L)).thenReturn(Optional.of(to));

        assertThatThrownBy(() -> service.executeTransfer(10L, dto))
                .isInstanceOf(ForbiddenTransactionException.class)
                .hasMessageContaining("not enough");
    }

}
