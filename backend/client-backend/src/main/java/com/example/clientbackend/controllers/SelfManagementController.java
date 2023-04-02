package com.example.clientbackend.controllers;

import com.example.clientbackend.appuser.AppUserService;
import com.example.clientbackend.appuser.model.AppUser;
import com.example.clientbackend.token.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/self_management")
@AllArgsConstructor
public class SelfManagementController {

    private final AppUserService appUserService;

    private final JwtService jwtService;


    @GetMapping(path = "get_current_user")
    public Optional<AppUser> getCurrentUser(HttpServletRequest request) {
        return getCurrentUserDetails(request);
    }

    @DeleteMapping(path = "delete_current_user")
    public void deleteCurrentUser(HttpServletRequest request) {
        Optional<AppUser> appUser = getCurrentUserDetails(request);
        SecurityContextHolder.clearContext();
        appUser.map(user ->
                appUserService.deleteUserByEmail(user.getEmail()));
    }

    @PutMapping(path = "update_current_username")
    public String updateCurrentUsername(HttpServletRequest request, @RequestParam String firstName, @RequestParam String lastName) {
        Optional<AppUser> appUser = getCurrentUserDetails(request);
        return appUser.map(user ->
                appUserService.updateNameByEmail(firstName, lastName, user.getEmail())).orElse(null);
    }

    @PutMapping(path = "update_current_email")
    public String updateCurrentEmail(HttpServletRequest request, @RequestParam String email) {
        Optional<AppUser> appUser = getCurrentUserDetails(request);
        return appUser.map(user ->
                appUserService.updateEmailById(user.getUid(), email)).orElse(null);
    }

    @PutMapping(path = "update_current_password")
    public String updateCurrentPassword(HttpServletRequest request, @RequestParam String password) {
        Optional<AppUser> appUser = getCurrentUserDetails(request);
        return appUser.map(user ->
                appUserService.updatePasswordByEmail(user.getEmail(), password)).orElse(null);
    }

    private Optional<AppUser> getCurrentUserDetails(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);
            if (userEmail != null) {
                return appUserService.getUserByEmail(userEmail);
            }
        }
        return Optional.empty();
    }
}