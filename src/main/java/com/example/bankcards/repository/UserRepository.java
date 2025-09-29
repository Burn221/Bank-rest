package com.example.bankcards.repository;

import com.example.bankcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Класс репозитория для работы с пользователями*/
@Repository
public interface UserRepository  extends JpaRepository<User, Long> {

    /**
     * Поиск пользователя по его имени
     * @param username Принимает строку имени пользователя
     * @return Возвращает Optional полученого пользователя
     */
    Optional<User> findByUsername(String username);
}
