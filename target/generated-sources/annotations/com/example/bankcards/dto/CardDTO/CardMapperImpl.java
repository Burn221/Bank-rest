package com.example.bankcards.dto.CardDTO;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Status;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-24T04:09:05+0600",
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

        CardResponse cardResponse = new CardResponse();

        cardResponse.setUserId( cardUserId( card ) );
        cardResponse.setId( card.getId() );
        cardResponse.setOwnerName( card.getOwnerName() );
        cardResponse.setExpiryMonth( card.getExpiryMonth() );
        cardResponse.setExpiryYear( card.getExpiryYear() );
        cardResponse.setStatus( card.getStatus() );
        cardResponse.setBalanceMinor( card.getBalanceMinor() );
        cardResponse.setCurrency( card.getCurrency() );
        cardResponse.setCreatedAt( card.getCreatedAt() );

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
