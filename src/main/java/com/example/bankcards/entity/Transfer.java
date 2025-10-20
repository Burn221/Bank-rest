package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.TransferStatus;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "transfers")
@Valid
@RedisHash("Transfer")
public class Transfer  {

    /** id первичный ключ  */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** id карты из которой будет совершаться перевод, поле имеет внешний ключ на таблицу cards(id) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_card_id", nullable = false)
    @ToString.Exclude
    private Card fromCard;

    /** id карты в которую будут переводиться деньги, поле имеет внешний ключ на таблицу cards(id) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_card_id", nullable = false)
    @ToString.Exclude
    private Card toCard;

    /** Количество переводимых денег */
    @Column(name = "amount_minor", nullable = false)
    private Long amountMinor=0L;


    /** Валюта перевода */
    @Column(name = "currency", nullable = false)
    private String currency;


    /** Статус перевода: FAILED/PENDING/SUCCESS */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransferStatus transferStatus;

    /** Дата создания перевода */
    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;


    /**
     * Переопределяет equals для сущности, сравнивает два объекта по id
     * @param o Принимает объект для сравнения
     * @return возвращает true если id объектов совпадает и false если нет
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Transfer transfer = (Transfer) o;
        return getId() != null && Objects.equals(getId(), transfer.getId());
    }

    /**
     * Переопределяет hashCode() для корректного сравнения
     * @return возвращает корректный хэшкод
     */
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
