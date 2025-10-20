package com.example.bankcards.service;

import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.CardDTO.*;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Status;

import com.example.bankcards.exception.exceptions.ActivatedException;
import com.example.bankcards.exception.exceptions.ForbiddenTransactionException;
import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.Impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestCardServiceImpl {

    @Mock private CardRepository cardRepository;
    @Mock private UserRepository userRepository;
    @Mock private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl service;

    private User user;
    private Card card;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(10L);
        user.setUsername("user");
        user.setEnabled(true);

        card = new Card();
        card.setId(10L);
        card.setUser(user);
        card.setOwnerName("user");
        card.setStatus(Status.ACTIVE);
        card.setExpiryMonth((short) 12);
        card.setExpiryYear((short) 30);
        card.setCurrency("KZT");
        card.setBalanceMinor(500_00L);
        card.setPanEncrypted("M82jDodU3kmtoto4TAhPE58z0UKmA0AvtedH1iz5Udcdv1UMkDr35ovvcV8=");
        card.setPanLast4("1234");
        card.setCreatedAt(LocalDateTime.now());
    }


    //User


    @Test
    @DisplayName("USER: createCardUser создаёт карту пользователю")
    void createCardUser_ok() {
        CreateCardRequest dto = new CreateCardRequest(10L,"user", "KZT");
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> {
            Card c = inv.getArgument(0);
            c.setId(10L);
            return c;
        });


        when(cardMapper.toEntity(eq(dto), eq(user))).thenAnswer(inv -> {
            CreateCardRequest d = inv.getArgument(0);
            Card c = new Card();
            c.setUser(user);
            c.setOwnerName(d.ownerName());
            c.setExpiryMonth((short)9);
            c.setExpiryYear((short)2028);
            c.setCurrency(d.currency());
            c.setStatus(Status.ACTIVE);
            c.setCreatedAt(LocalDateTime.now());
            return c;
        });

        CardResponse mapped = new CardResponse();
        mapped.setId(10L);
        mapped.setUserId(10L);
        mapped.setPanMasked("**** **** **** 1234");
        when(cardMapper.toResponse(any(Card.class))).thenReturn(mapped);

        CardResponse resp = service.createCardUser(10L,dto);

        assertThat(resp.getId()).isEqualTo(10L);
        verify(userRepository).findById(10L);
        verify(cardRepository).save(any(Card.class));
        verify(cardMapper).toResponse(any(Card.class));
    }
    @Test
    @DisplayName("USER: showMyCardsUser возвращает страницу своих карт")
    void showMyCardsUser_ok() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Card> page = new PageImpl<>(List.of(card), pageable, 1);
        when(cardRepository.findByUser_Id(eq(user.getId()), eq(pageable))).thenReturn(page);

        CardResponse mapped = new CardResponse();
        mapped.setId(card.getId());
        mapped.setUserId(user.getId());
        mapped.setPanMasked("**** **** **** 1234");
        mapped.setStatus(card.getStatus());
        mapped.setCurrency(card.getCurrency());
        when(cardMapper.toResponse(card)).thenReturn(mapped);

        Page<CardResponse> result = service.showMyCardsUser(user.getId(), pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(10L);
        verify(cardRepository).findByUser_Id(user.getId(), pageable);
        verify(cardMapper).toResponse(card);
    }

    @Test
    @DisplayName("USER: getMyCardUser отдаёт свою карту; бросает NotFound для чужой")
    void getMyCardUser_ok_and_forbidden() {
        when(cardRepository.findByIdAndUser_Id(10L,10L)).thenReturn(Optional.of(card));

        CardResponse mapped = new CardResponse();
        mapped.setId(card.getId());
        mapped.setUserId(user.getId());
        mapped.setPanMasked("**** **** **** 1234");
        when(cardMapper.toResponse(card)).thenReturn(mapped);

        //ok
        CardResponse result = service.getMyCardUser(10L, 10L);
        assertThat(result.getId()).isEqualTo(10L);

        // Not found
        assertThatThrownBy(() -> service.getMyCardUser(999L, 10L))
                .isInstanceOf(NotFoundException.class);


    }


    @Test
    @DisplayName("USER: showMyBalanceUser возвращает баланс своей карты")
    void showMyBalanceUser_ok() {
        BalanceResponse balance = new BalanceResponse();
        balance.setCurrentBalance(card.getBalanceMinor());
        balance.setCurrency(card.getCurrency());
        when(cardRepository.findBalanceUser(10L, 10L)).thenReturn(Optional.of(balance));

        BalanceResponse result = service.showMyBalanceUser(10L, 10L);
        assertThat(result.getCurrentBalance()).isEqualTo(500_00L);
        assertThat(result.getCurrency()).isEqualTo("KZT");
        verify(cardRepository).findBalanceUser(10L, 10L);
    }

    @Test
    @DisplayName("USER: showMyBalanceUser NotFound, если карты нет/чужая")
    void showMyBalanceUser_notFound() {
        when(cardRepository.findBalanceUser(10L, 100L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.showMyBalanceUser(10L, 100L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("USER: blockRequestUser ставит статус BLOCKED для своей карты")
    void blockRequestUser_ok() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));
        BlockCardRequest request= new BlockCardRequest("pass","pass");
        CardResponse mapped = new CardResponse();
        mapped.setId(10L);
        mapped.setStatus(Status.BLOCKED);
        when(cardMapper.toResponse(any(Card.class))).thenReturn(mapped);

        CardResponse resp = service.blockRequestUser(10L, 10L, request);

        assertThat(resp.getStatus()).isEqualTo(Status.BLOCKED);
        assertThat(card.getStatus()).isEqualTo(Status.BLOCKED);
        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("USER: blockRequestUser illegal для чужой карты")
    void blockRequestUser_forbidden() {
        when(cardRepository.findById(100L)).thenReturn(Optional.of(card));
        BlockCardRequest request= new BlockCardRequest("pass","pass");
        assertThatThrownBy(() -> service.blockRequestUser(999L, 100L, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    //Admin

    @Test
    @DisplayName("ADMIN: createCardAdmin создаёт карту пользователю")
    void createCardAdmin_ok() {
        CreateCardRequest dto = new CreateCardRequest(10L,"user", "KZT");
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> {
            Card c = inv.getArgument(0);
            c.setId(10L);
            return c;
        });


        when(cardMapper.toEntity(eq(dto), eq(user))).thenAnswer(inv -> {
            CreateCardRequest d = inv.getArgument(0);
            Card c = new Card();
            c.setUser(user);
            c.setOwnerName(d.ownerName());
            c.setExpiryMonth((short)9);
            c.setExpiryYear((short)2028);
            c.setCurrency(d.currency());
            c.setStatus(Status.ACTIVE);
            c.setCreatedAt(LocalDateTime.now());
            return c;
        });

        CardResponse mapped = new CardResponse();
        mapped.setId(10L);
        mapped.setUserId(10L);
        mapped.setPanMasked("**** **** **** 1234");
        when(cardMapper.toResponse(any(Card.class))).thenReturn(mapped);

        CardResponse resp = service.createCardAdmin(dto);

        assertThat(resp.getId()).isEqualTo(10L);
        verify(userRepository).findById(10L);
        verify(cardRepository).save(any(Card.class));
        verify(cardMapper).toResponse(any(Card.class));
    }


    @Test
    @DisplayName("ADMIN: updateCardAdmin обновляет поля и сохраняет")
    void updateCardAdmin_ok() {
        UpdateCardRequest dto = new UpdateCardRequest("user");
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
        when(cardRepository.save(card)).thenReturn(card);
        CardResponse mapped = new CardResponse();
        mapped.setId(10L);
        mapped.setStatus(Status.BLOCKED);
        when(cardMapper.toResponse(card)).thenReturn(mapped);

        CardResponse resp = service.updateCardAdmin(10L, dto);
        assertThat(resp.getStatus()).isEqualTo(Status.BLOCKED);
        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("ADMIN: deleteCardAdmin удаляет карту, если она не ACTIVE")
    void deleteCardAdmin_ok() {

        card.setStatus(Status.BLOCKED);
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));


        service.deleteCardAdmin(10L);

        verify(cardRepository).findById(10L);
        verify(cardRepository).deleteById(10L);
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    @DisplayName("ADMIN: deleteCardAdmin NotFound, если карты нет")
    void deleteCardAdmin_notFound() {
        when(cardRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteCardAdmin(10L))
                .isInstanceOf(NotFoundException.class);

        verify(cardRepository).findById(10L);
        verify(cardRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("ADMIN: deleteCardAdmin ActivatedException, если карта ACTIVE")
    void deleteCardAdmin_activeForbidden() {
        card.setStatus(Status.ACTIVE);
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> service.deleteCardAdmin(10L))
                .isInstanceOf(ActivatedException.class)
                .hasMessageContaining("must be disabled");

        verify(cardRepository).findById(10L);
        verify(cardRepository, never()).deleteById(anyLong());
    }


    @Test
    @DisplayName("ADMIN: blockCardAdmin ставит статус BLOCKED для  карты")
    void blockCardAdmin_ok() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));
        CardResponse mapped = new CardResponse();
        mapped.setId(10L);
        mapped.setStatus(Status.BLOCKED);
        when(cardMapper.toResponse(any(Card.class))).thenReturn(mapped);

        CardResponse resp = service.blockCardAdmin(10L);

        assertThat(resp.getStatus()).isEqualTo(Status.BLOCKED);
        assertThat(card.getStatus()).isEqualTo(Status.BLOCKED);
        verify(cardRepository).save(card);
    }

    @Test
    @DisplayName("ADMIN: activateCardAdmin ставит статус ACTIVE для  карты")
    void activateCardAdmin_ok() {
        when(cardRepository.findById(10L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));
        CardResponse mapped = new CardResponse();
        mapped.setId(10L);
        mapped.setStatus(Status.ACTIVE);
        when(cardMapper.toResponse(any(Card.class))).thenReturn(mapped);

        CardResponse resp = service.activateCardAdmin(10L);

        assertThat(resp.getStatus()).isEqualTo(Status.ACTIVE);

        assertThat(card.getStatus()).isEqualTo(Status.ACTIVE);
        verify(cardRepository).save(card);
    }







}
