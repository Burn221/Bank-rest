package com.example.bankcards.repository;

import com.example.bankcards.dto.BalanceResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.Status;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
/** Класс репозитория для манипуляциями с картами*/
@Repository
public interface CardRepository extends JpaRepository<Card,Long> {

    /** Метод осуществляющий jpql запрос для получения данных карт для админа с пагинацией
     * @param userId Принимает id пользователя если необходимо получить карты конкретного пользователя
     * @param status Принимает status если необходимо получить карты с определенным статсуом
     * @param last4 Принимает 4 последние цифры карты если необходимо поулчить карты с определенными 4мя последними цифрами
     * @param pageable Принимает pageable для тонкой настройки отображения страницы
     * @return Возвращает страницу Page карт*/
    @Query("""
    select c from Card c
    where (:userId is null or c.user.id=:userId)
    and (:status is null or c.status=:status)
    and (:last4 is null or c.panLast4=:last4)
""")

    Page<Card> findAdminFiltered(@Param("userId") Long userId,
                                         @Param("status") Status status,
                                         @Param("last4") String last4,
                                         Pageable pageable
                                         );

    /** Поиск по id карты и id пользователя
     * @param cardId принимает id карты
     * @param userId принимает id пользователя
     * @return Возвращает Optional полученой карты*/
    Optional<Card> findByIdAndUser_Id(Long cardId, Long userId);


    /** Поиск по id пользователя
     * @param userId принимает id пользователя
     * @param pageable Принимает pageable для тонкой настройки отображения страницы
     * @return Возвращает страницу Page карт*/
    Page<Card> findByUser_Id(Long userId, Pageable pageable);

    /** Проверка существует ли пользователь с данным id
     * @param userId id пользователя
     * @return Возвращает true или false*/
    boolean existsByUser_Id(Long userId);


    /** Осуществляет JPQL запрос который получает баланс и валюту по пользователю и его карте
     * @param userId Принимает id пользователя
     * @param cardId Принимает id карты
     * @return Возвращает Optional типа BalanceResponse который содержит полученые поля*/
    @Query("""
    select c.balanceMinor, c.currency from Card c
    where(c.id=:cardId) and (c.user.id=:userId)
""")
    Optional <BalanceResponse> findBalanceUser(
            @Param("userId") Long userId,
            @Param("cardId") Long cardId
                                    );



    /** Поиск карты по id, осуществляет блокирование карты на время выполнения транзакции для предотвращения перезаписи или конфликта данных если другие сущности попытаются получить доступ к ней
     * @param id Принимает id карты для изменения
     * @return Возвращает Optional полученой карты*/
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Card c where c.id = :id")
    Optional<Card> findForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select c from Card c where c.id=:panEncrypted
""")


    Optional<Card> findForUpdateByPan(@Param("panEncrypted") String panEncrypted);


}
