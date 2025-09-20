package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import jakarta.persistence.LockModeType;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card,Long> {

    Optional<Card> findByIdAndUser_Id(Long cardId, Long userId);

    Page<Card> findByUser_Id(Long userId, Pageable pageable);

    Page<Card> findAll(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Card c where c.id = :id")
    Optional<Card> findForUpdate(@Param("id") Long id);


}
