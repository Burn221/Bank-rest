package com.example.bankcards.service.Impl;

import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.dto.CardDTO.CardMapper;
import com.example.bankcards.dto.CardDTO.CardResponse;
import com.example.bankcards.dto.CardDTO.CreateCardRequest;
import com.example.bankcards.dto.CardDTO.UpdateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Status;
import com.example.bankcards.exception.ForbiddenTransactionException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.AesGcm;
import com.example.bankcards.util.Mask;
import com.example.bankcards.util.PanGenerator;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CardServiceImpl implements CardService {

    private CardRepository cardRepository;
    private UserRepository userRepository;
    private CardMapper mapper;



    @Override
    @Transactional
    public CardResponse createCardAdmin(CreateCardRequest dto) {

        User user= userRepository.findById(dto.userId())
                .orElseThrow(()-> new NotFoundException("User not found"));

        String generatedPan= PanGenerator.generatePan();

        Card card= new Card();
        card.setUser(user);
        card.setPanEncrypted(AesGcm.encryptToBase64(generatedPan));
        card.setPanLast4(generatedPan.substring(generatedPan.length() - 4));
        card.setOwnerName(dto.ownerName());
        card.setExpiryMonth((short) LocalDateTime.now().getMonthValue());
        card.setExpiryYear( (short) (LocalDateTime.now().getYear()+ 3) );
        card.setStatus(Status.ACTIVE);
        card.setCurrency(dto.currency());
        card.setCreatedAt(LocalDateTime.now());

        return mapper.toResponse(cardRepository.save(card));
    }


    @Override
    @Transactional
    public CardResponse updateCardAdmin (Long cardId, UpdateCardRequest dto) {

        Card card= cardRepository.findById(cardId)
                .orElseThrow(()-> new NotFoundException("Card not found"));

        card.setOwnerName(dto.ownerName());

        return mapper.toResponse(cardRepository.save(card));
    }


    @Override
    @Transactional
    public void deleteCardAdmin(Long cardId) {
        cardRepository.deleteById(cardId);

    }

    @Override
    @Transactional
    public CardResponse blockCardAdmin(Long cardId) {
        Card card= cardRepository.findById(cardId)
                .orElseThrow(()-> new NotFoundException("Card not found"));

        card.setStatus(Status.BLOCKED);

        return mapper.toResponse(cardRepository.save(card));
    }

    @Override
    @Transactional
    public CardResponse activateCardAdmin(Long cardId) {
        Card card= cardRepository.findById(cardId)
                .orElseThrow(()-> new NotFoundException("Card not found"));

        card.setStatus(Status.ACTIVE);

        return mapper.toResponse(cardRepository.save(card));
    }


    @Transactional
    @Override
    public Page<CardResponse> showAllCardsAdmin(Pageable pageable, Long userId, Status status, String last4) {
        return cardRepository.findAdminFiltered(userId,status,last4,pageable)
                .map(card -> {
                    CardResponse dto = mapper.toResponse(card);
                    dto.setPanMasked(Mask.mask(card.getPanLast4()));
                    return dto;
                });
    }



    @Override
    public CardResponse getByIdAdmin(Long cardId) {
        Card card=cardRepository.findById(cardId).orElseThrow(()-> new NotFoundException("Card not found"));
        CardResponse response= mapper.toResponse(card);

        response.setPanMasked(Mask.mask(card.getPanLast4()));
        return response;

    }

    @Transactional
    @Override
    public CardResponse createCardUser(Long userId, CreateCardRequest dto) {
        User user= userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("User not found"));

        String generatedPan= PanGenerator.generatePan();

        Card card= new Card();
        card.setUser(user);
        card.setPanEncrypted(AesGcm.encryptToBase64(generatedPan));
        card.setPanLast4(generatedPan.substring(generatedPan.length() - 4));
        card.setOwnerName(dto.ownerName());
        card.setExpiryMonth((short) LocalDateTime.now().getMonthValue());
        card.setExpiryYear( (short) (LocalDateTime.now().getYear()+ 3) );
        card.setStatus(Status.ACTIVE);
        card.setCurrency(dto.currency());
        card.setCreatedAt(LocalDateTime.now());

        cardRepository.save(card);

        CardResponse response= mapper.toResponse(card);
        response.setPanPlain(AesGcm.decryptFromBase64(card.getPanEncrypted()));

        return response;
    }

    @Override
    public Page<CardResponse> showMyCardsUser(Long userId, Pageable pageable) {
        return cardRepository.findByUser_Id(userId,pageable)
                .map(card -> {
                    String last4 = card.getPanLast4();
                    CardResponse dto = mapper.toResponse(card);
                    dto.setPanMasked("**** **** **** " + last4);
                    return dto;
                });

    }

    @Override
    public CardResponse getMyCardUser(Long cardId, Long userId) {
        Card card= cardRepository.findByIdAndUser_Id(cardId, userId)
                .orElseThrow(()-> new NotFoundException("Card not found"));

        CardResponse response= mapper.toResponse(card);
        response.setPanMasked(Mask.mask((card.getPanLast4())));
        response.setPanPlain(AesGcm.decryptFromBase64(card.getPanEncrypted()));

        return response;
    }

    @Override
    public BalanceResponse showMyBalanceUser(Long userId, Long cardId) {

        return cardRepository.findBalanceUser(userId,cardId)
                .orElseThrow(()-> new NotFoundException("Card not found"));
    }

    @Override
    @Transactional
    public CardResponse blockRequestUser(Long userId, Long cardId) {

        Card card= cardRepository.findById(cardId)
                .orElseThrow(()-> new NotFoundException("Card not found"));

        if(!card.getUser().getId().equals(userId)) throw new ForbiddenTransactionException("User is incorrect or not found");

        card.setStatus(Status.BLOCKED);

        return mapper.toResponse(cardRepository.save(card));
    }
}
