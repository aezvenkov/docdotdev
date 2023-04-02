package com.example.clientbackend.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.clientbackend.appuser.AppUserService;
import com.example.clientbackend.appuser.model.AppUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.clientbackend.Constants.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "api/token_controller")
@AllArgsConstructor
public class JwtTokenController {

    private final AppUserService appUserService;

    @GetMapping(path = "refresh_token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(HEADER_BEARER)) {
            try {
                String refreshToken = authorizationHeader.substring(HEADER_BEARER.length());
                Algorithm algorithm = Algorithm.HMAC256(ALGORITHM_SECRET.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();

                Optional<AppUser> appUser = appUserService.getUserByEmail(username);
                if (appUser.isPresent()) {
                    String accessToken = JWT.create()
                            .withSubject(appUser.get().getUsername())
                            .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                            .withIssuer(request.getRequestURL().toString())
                            .withClaim(CLAIM_ROLES,
                                    appUser.get().getAuthorities().stream()
                                            .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                            .sign(algorithm);

                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("access_token", accessToken);
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                } else throw new RuntimeException("User is not found");

            } catch (Exception exception) {
                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());

                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing!");
        }
    }
}
