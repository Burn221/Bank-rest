package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name="cards")
@Valid
public class Card {

    /** id первичный ключ */
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    /** id пользователя владеющего картой, ссылается на таблицу users(id)*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;


    /** Зашифрованый PAN карты*/
    @Column(name = "pan_encrypted", nullable = false, unique = true)
    private String panEncrypted;

    /** 4 последние цифры карты*/
    @Column(name = "pan_last4", nullable = false, length = 4)
    private String panLast4;


    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    /** Месяц истечения срока карты*/
    @Column(name = "expiry_month", nullable = false)
    private Short expiryMonth;

    /** Год истечения срока карты*/
    @Column(name = "expiry_year", nullable = false)
    private Short expiryYear;

    /** Статус карты: ACTIVE/BLOCKED*/
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    /** Баланс на карте */
    @Column(name = "balance_minor", nullable = false)
    private Long balanceMinor=0L;

    /** Валюта карты*/
    @Column(name = "currency", nullable = false)
    private String currency;


    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /** Дата создания карты*/
    @Column(name = "created_at", nullable = false, updatable = false)
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
        Card card = (Card) o;
        return getId() != null && Objects.equals(getId(), card.getId());
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
