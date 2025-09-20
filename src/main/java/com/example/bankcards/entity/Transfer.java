package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.TransferStatus;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transfers")
@Valid
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_card_id", nullable = false)
    private Card fromCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_card_id", nullable = false)
    private Card toCard;

    @Column(name = "amount_minor", nullable = false)
    private Long amountMinor=0L;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransferStatus transferStatus;

    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;



}
