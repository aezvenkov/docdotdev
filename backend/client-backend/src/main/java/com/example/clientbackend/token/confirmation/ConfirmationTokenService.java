package com.example.clientbackend.token.confirmation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public List<ConfirmationToken> getExpiredTokens(LocalDateTime localDateTime) {
        return confirmationTokenRepository.findExpiredTokens(localDateTime);
    }

    public void deleteExpiredTokens(LocalDateTime localDateTime) {
        confirmationTokenRepository.deleteExpiredTokens(localDateTime);
    }

    public int setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }
}

