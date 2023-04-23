package com.example.clientbackend.requests;

public record AuthenticationRequest(
        String email,
        String password,
        String deviceToken
) {

}
