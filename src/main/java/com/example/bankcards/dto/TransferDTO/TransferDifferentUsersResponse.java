package com.example.bankcards.dto.TransferDTO;

import lombok.Data;

import java.io.Serializable;

@Data
public class TransferDifferentUsersResponse implements Serializable {
    Long fromCardId;
    Long toCardId;
    String fromOwnerName;
    String toOwnerName;
    String fromPanMasked;
    String toPanMasked;
    Long amount;




}
