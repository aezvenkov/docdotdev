package com.example.clientbackend.requests;

public record RegistrationRequest(
        String firstName,
        String lastName,
        String email,
        String password) {
}
