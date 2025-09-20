package com.example.bankcards.dto.TransferDTO;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransferMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "fromCard", source= "fromCard"),
            @Mapping(target = "toCard", source= "toCard"),
            @Mapping(target= "amountMinor", source = "dto.amountMinor"),
            @Mapping(target = "currency", source= "dto.currency"),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "createdAt", ignore = true)
    })
    Transfer toEntity(Card fromCard, Card toCard, CreateTransferRequest dto);

    @Mappings({
            @Mapping(target = "fromCardId", source = "fromCard.id"),
            @Mapping(target = "toCardId", source = "toCard.id")
    })
    TransferResponse toResponse(Transfer transfer);
}
