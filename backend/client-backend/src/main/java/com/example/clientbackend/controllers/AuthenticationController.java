package com.example.clientbackend.controllers;

import com.example.clientbackend.appuser.AuthenticationService;
import com.example.clientbackend.requests.AuthenticationRequest;
import com.example.clientbackend.requests.AuthenticationResponse;
import com.example.clientbackend.requests.RegistrationRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(path = "registration")
    public AuthenticationResponse register(@RequestBody RegistrationRequest request) {
        return authenticationService.register(request);
    }

    @GetMapping(path = "confirm")
    public String confirm(@RequestParam("token") String token) {
        return authenticationService.confirmToken(token);
    }

    @PostMapping(path = "authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
