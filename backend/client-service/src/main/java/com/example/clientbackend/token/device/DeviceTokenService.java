package com.example.clientbackend.token.device;

import com.example.clientbackend.appuser.model.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;

    public void addDeviceToken(String deviceToken, AppUser appUser) {
        if(deviceTokenRepository.findByToken(deviceToken).isEmpty()) {
            DeviceToken token = DeviceToken.builder()
                    .token(deviceToken).appUser(appUser).build();
            deviceTokenRepository.save(token);
        }
    }

    public List<DeviceToken> getDeviceTokens(AppUser appUser) {
        return deviceTokenRepository.findAllByAppUser(appUser);
    }
}
