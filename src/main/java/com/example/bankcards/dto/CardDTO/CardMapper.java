package com.example.bankcards.dto.CardDTO;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;

import org.mapstruct.*;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardMapper {


    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "user", source = "user"),
            @Mapping(target = "ownerName", source = "dto.ownerName"),
            @Mapping(target = "expiryMonth", source = "dto.expiryMonth"),
            @Mapping(target = "expiryYear", source = "dto.expiryYear"),
            @Mapping(target = "currency", source = "dto.currency"),
            @Mapping(target = "status", constant = "ACTIVE"),
            @Mapping(target = "balanceMinor", constant = "0L"),

            @Mapping(target = "panEncrypted", ignore = true),
            @Mapping(target = "panLast4", ignore = true),
            @Mapping(target = "createdAt", ignore = true)
    })
    Card toEntity(CreateCardRequest dto, User user);

    @Mappings({
            @Mapping(target = "userId", source = "user.id"),
            @Mapping(target= "panEncrypted", ignore = true)
    })
    CardResponse toResponse(Card card);






}
