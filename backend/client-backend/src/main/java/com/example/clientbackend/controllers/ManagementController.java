package com.example.clientbackend.controllers;

import com.example.clientbackend.appuser.AppUserService;
import com.example.clientbackend.appuser.model.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/management")
@AllArgsConstructor
public class ManagementController {

    private final AppUserService appUserService;

    @GetMapping(path = "get_all_users")
    public List<AppUser> getAllUsers() {
        return appUserService.getAllUsers();
    }

    @GetMapping(path = "get_user_by_id")
    public Optional<AppUser> getUserById(@RequestParam long id) {
        return appUserService.getUserById(id);
    }

    @GetMapping(path = "get_user_by_email")
    public Optional<AppUser> getUserByEmail(@RequestParam String email) {
        return appUserService.getUserByEmail(email);
    }

    @DeleteMapping(path = "delete_user_by_id")
    public String deleteUserById(@RequestParam long id) {
        return appUserService.deleteUserById(id);
    }

    @DeleteMapping(path = "delete_user_by_email")
    public String deleteUserByEmail(@RequestParam String email) {
        return appUserService.deleteUserByEmail(email);
    }

    @PutMapping(path = "update_user_email_by_id")
    public String updateEmailById(@RequestParam long id, @RequestParam String email) {
        return appUserService.updateEmailById(id, email);
    }

    @PutMapping(path = "update_name_by_email")
    public String updateNameByEmail(@RequestParam String email, @RequestParam String firstName,
                                    @RequestParam String lastName) {
        return appUserService.updateNameByEmail(email, firstName, lastName);
    }
}
