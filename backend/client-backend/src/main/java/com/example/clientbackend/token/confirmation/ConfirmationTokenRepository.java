package com.example.clientbackend.token.confirmation;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c SET c.confirmedAt = ?2 WHERE c.token = ?1")
    int updateConfirmedAt(String token, LocalDateTime confirmedAt);

    @Transactional
    @Modifying
    @Query("DELETE FROM ConfirmationToken WHERE confirmedAt IS NULL and expiresAt < :localDateTime")
    void deleteExpiredTokens(@Param("localDateTime") LocalDateTime localDateTime);

    /**
     * Native query for SELECT ALL with some parameter
     */
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "SELECT * FROM confirmation_token WHERE confirmed_at IS NULL and expires_at < :localDateTime")
    List<ConfirmationToken> findExpiredTokens(@Param("localDateTime") LocalDateTime localDateTime);
}
