package com.ninehub.dreamshops.repositry;

import com.ninehub.dreamshops.model.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ValidationRepository extends JpaRepository<Validation, Long> {
    Optional<Validation> findByCodeAndUsedFalse(String code);

    List<Validation> findByUserEmailAndUsedFalse(String email);

    @Query("SELECT v FROM Validation v WHERE v.expiresAt < :now AND v.used = false")
    List<Validation> findExpiredValidations(LocalDateTime now);

    void deleteByExpiresAtBeforeAndUsedTrue(LocalDateTime dateTime);
}