package com.example.clientbackend.token.device;

import com.example.clientbackend.appuser.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Integer> {

    List<DeviceToken> findAllByAppUser(AppUser appUser);

    Optional<DeviceToken> findByToken(String token);

}
