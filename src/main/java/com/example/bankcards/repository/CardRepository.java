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

@Repository
public interface CardRepository extends JpaRepository<Card,Long> {

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

    Optional<Card> findByIdAndUser_Id(Long cardId, Long userId);

    Page<Card> findByUser_Id(Long userId, Pageable pageable);

    boolean existsByUser_Id(Long userId);



    @Query("""
    select c.balanceMinor, c.currency from Card c
    where(c.id=:cardId) and (c.user.id=:userId)
""")
    Optional <BalanceResponse> findBalanceUser(
            @Param("userId") Long userId,
            @Param("cardId") Long cardId
                                    );




    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Card c where c.id = :id")
    Optional<Card> findForUpdate(@Param("id") Long id);


}
