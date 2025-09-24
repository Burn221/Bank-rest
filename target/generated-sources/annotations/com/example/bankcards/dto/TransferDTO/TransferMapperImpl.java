package com.example.bankcards.dto.TransferDTO;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.enums.TransferStatus;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-09-24T04:09:05+0600",
    comments = "version: 1.6.3, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class TransferMapperImpl implements TransferMapper {

    @Override
    public Transfer toEntity(Card fromCard, Card toCard, CreateTransferRequest dto) {
        if ( fromCard == null && toCard == null && dto == null ) {
            return null;
        }

        Transfer transfer = new Transfer();

        if ( dto != null ) {
            transfer.setAmountMinor( dto.amountMinor() );
            transfer.setCurrency( dto.currency() );
        }
        transfer.setFromCard( fromCard );
        transfer.setToCard( toCard );

        return transfer;
    }

    @Override
    public TransferResponse toResponse(Transfer transfer) {
        if ( transfer == null ) {
            return null;
        }

        Long fromCardId = null;
        Long toCardId = null;
        Long id = null;
        Long amountMinor = null;
        String currency = null;
        TransferStatus transferStatus = null;
        LocalDateTime createdAt = null;

        fromCardId = transferFromCardId( transfer );
        toCardId = transferToCardId( transfer );
        id = transfer.getId();
        amountMinor = transfer.getAmountMinor();
        currency = transfer.getCurrency();
        transferStatus = transfer.getTransferStatus();
        createdAt = transfer.getCreatedAt();

        TransferResponse transferResponse = new TransferResponse( id, fromCardId, toCardId, amountMinor, currency, transferStatus, createdAt );

        return transferResponse;
    }

    private Long transferFromCardId(Transfer transfer) {
        Card fromCard = transfer.getFromCard();
        if ( fromCard == null ) {
            return null;
        }
        return fromCard.getId();
    }

    private Long transferToCardId(Transfer transfer) {
        Card toCard = transfer.getToCard();
        if ( toCard == null ) {
            return null;
        }
        return toCard.getId();
    }
}
