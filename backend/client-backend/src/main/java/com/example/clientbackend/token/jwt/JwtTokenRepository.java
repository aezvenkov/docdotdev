package com.example.clientbackend.token.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JwtTokenRepository extends JpaRepository<JwtToken, Integer> {

    @Query(value = """
            SELECT t FROM JwtToken t INNER JOIN AppUser u\s
            ON t.user.uid = u.uid\s
            WHERE u.uid = :id AND (t.expired = false OR t.revoked = false)\s
            """)
    List<JwtToken> findAllValidTokenByUser(long id);

    Optional<JwtToken> findByToken(String token);
}