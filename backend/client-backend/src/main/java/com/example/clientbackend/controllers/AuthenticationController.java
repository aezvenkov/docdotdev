package com.example.clientbackend.controllers;

import com.example.clientbackend.appuser.AuthenticationService;
import com.example.clientbackend.requests.AuthenticationRequest;
import com.example.clientbackend.requests.AuthenticationResponse;
import com.example.clientbackend.requests.RegistrationRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api/v1/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(path = "registration")
    public Mono<AuthenticationResponse> register(@RequestBody RegistrationRequest request) {
        return Mono.just(authenticationService.register(request));
    }

    @GetMapping(path = "confirm")
    public Mono<String> confirm(@RequestParam("token") String token) {
        return authenticationService.confirmToken(token);
    }

    @PostMapping(path = "authenticate")
    public Mono<ResponseEntity<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest request) {
        return Mono.just(ResponseEntity.ok(authenticationService.authenticate(request)));
    }
}
