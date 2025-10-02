package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс описывающий пользователей банка

*/

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Valid
public class User {

    /** id первичный ключ */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "username",unique = true, nullable = false, length = 100)
    private String username;

    /** Хэш представление пароля */
    @Column(name="password_hash", nullable = false)
    private String password_hash;


    /** Роль пользователя */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    /** Активен ли пользователь */
    @Column(name = "enabled", nullable = false)
    private boolean enabled=true;

    /** Дата создания пользователя */
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
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
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
