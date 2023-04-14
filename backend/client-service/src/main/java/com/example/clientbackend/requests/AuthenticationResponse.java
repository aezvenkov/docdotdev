package com.example.clientbackend.requests;

public record AuthenticationResponse(
        String confirmationToken,
        String jwtToken
) {
}
