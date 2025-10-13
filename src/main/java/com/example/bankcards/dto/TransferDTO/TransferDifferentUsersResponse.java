package com.example.bankcards.dto.TransferDTO;

import lombok.Data;

@Data
public class TransferDifferentUsersResponse {
    Long fromCardId;
    Long toCardId;
    String fromOwnerName;
    String toOwnerName;
    String fromPanMasked;
    String toPanMasked;
    Long amount;




}
