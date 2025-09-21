package com.example.bankcards.dto.CardDTO;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Status;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-20T21:59:22+0600",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class CardMapperImpl implements CardMapper {

    @Override
    public Card toEntity(CreateCardRequest dto, User user) {
        if ( dto == null && user == null ) {
            return null;
        }

        Card card = new Card();

        if ( dto != null ) {
            card.setOwnerName( dto.ownerName() );
            card.setExpiryMonth( dto.expiryMonth() );
            card.setExpiryYear( dto.expiryYear() );
            card.setCurrency( dto.currency() );
        }
        card.setUser( user );
        card.setStatus( Status.ACTIVE );
        card.setBalanceMinor( 0L );

        return card;
    }

    @Override
    public CardResponse toResponse(Card card) {
        if ( card == null ) {
            return null;
        }

        Long userId = null;
        Long id = null;
        String ownerName = null;
        Short expiryMonth = null;
        Short expiryYear = null;
        String status = null;
        Long balanceMinor = null;
        String currency = null;
        LocalDateTime createdAt = null;

        userId = cardUserId( card );
        id = card.getId();
        ownerName = card.getOwnerName();
        expiryMonth = card.getExpiryMonth();
        expiryYear = card.getExpiryYear();
        if ( card.getStatus() != null ) {
            status = card.getStatus().name();
        }
        balanceMinor = card.getBalanceMinor();
        currency = card.getCurrency();
        createdAt = card.getCreatedAt();

        String panMasked = null;

        CardResponse cardResponse = new CardResponse( id, userId, panMasked, ownerName, expiryMonth, expiryYear, status, balanceMinor, currency, createdAt );

        return cardResponse;
    }

    private Long cardUserId(Card card) {
        User user = card.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }
}
