package com.example.clientbackend.controllers;

import com.example.clientbackend.appuser.AppUserService;
import com.example.clientbackend.appuser.model.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/self_management")
@AllArgsConstructor
public class SelfManagementController {

    private final AppUserService appUserService;

    @GetMapping(path = "get_current_user")
    public Optional<AppUser> getCurrentUser() {
        return getCurrentUserDetails();
    }

    @DeleteMapping(path = "delete_current_user")
    public void deleteCurrentUser() {
        Optional<AppUser> appUser = getCurrentUserDetails();
        SecurityContextHolder.clearContext();
        appUser.map(user ->
                appUserService.deleteUserByEmail(user.getEmail()));
    }

    @PutMapping(path = "update_current_username")
    public String updateCurrentUsername(@RequestParam String firstName, @RequestParam String lastName) {
        Optional<AppUser> appUser = getCurrentUserDetails();
        return appUser.map(user ->
                appUserService.updateNameByEmail(firstName, lastName, user.getEmail())).orElse(null);
    }

    @PutMapping(path = "update_current_email")
    public String updateCurrentEmail(@RequestParam String email) {
        Optional<AppUser> appUser = getCurrentUserDetails();
        return appUser.map(user ->
                appUserService.updateEmailById(user.getUid(), email)).orElse(null);
    }

    @PutMapping(path = "update_current_password")
    public String updateCurrentPassword(@RequestParam String password) {
        Optional<AppUser> appUser = getCurrentUserDetails();
        return appUser.map(user ->
                appUserService.updatePasswordByEmail(user.getEmail(), password)).orElse(null);
    }

    private Optional<AppUser> getCurrentUserDetails() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal != null) {
            return appUserService.getUserByEmail(principal.toString());
        } else {
            return Optional.empty();
        }
    }

}