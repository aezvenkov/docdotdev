package com.example.clientbackend.appuser;

import com.example.clientbackend.appuser.model.AppUser;
import com.example.clientbackend.appuser.model.AppUserRole;
import com.example.clientbackend.config.KafkaProducerConfig;
import com.example.clientbackend.email.EmailValidator;
import com.example.clientbackend.kafka.MessageType;
import com.example.clientbackend.kafka.KafkaMessage;
import com.example.clientbackend.token.confirmation.ConfirmationToken;
import com.example.clientbackend.token.confirmation.ConfirmationTokenService;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.example.clientbackend.Constants.FROM_CLIENT_TO_MESSAGE_SERVICE_TOPIC;
import static com.example.clientbackend.Constants.USERS_COLLECTION;

@Service
@AllArgsConstructor
@Import(KafkaProducerConfig.class)
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "User with email %s not found";

    private final AppUserRepository appUserRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final ConfirmationTokenService confirmationTokenService;

    private EmailValidator emailValidator;

    private final KafkaProducerConfig kafkaProducerConfig;


    @Scheduled(timeUnit = TimeUnit.HOURS, fixedDelay = 1)
    public void scheduleUserCleaner() {
        System.out.printf(String.format("User cleaner started at: %s", LocalDateTime.now()));
        deleteExpiredData();
        System.out.print("User cleaner goes into sleep mode, the next start in 12 hours");
    }

    private void deleteExpiredData() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<ConfirmationToken> expiredTokens = confirmationTokenService.getExpiredTokens(now);
            for (ConfirmationToken token : expiredTokens) {
                appUserRepository.deleteById(token.getAppUser().getUid());
                sendKafkaMessage(new KafkaMessage(MessageType.USER_DELETED, token.getAppUser().getEmail()));
            }
            confirmationTokenService.deleteExpiredTokens(now);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public String signUser(AppUser appUser) {
        boolean userExists = appUserRepository
                .findByEmail(appUser.getEmail())
                .isPresent();

        if (userExists) {
            throw new IllegalStateException("Email already taken!");
        }

        String encodedPassword = passwordEncoder
                .encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);

        //TODO: Send confirmation token
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken =
                new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), appUser);
        confirmationTokenService.saveConfirmationToken(
                confirmationToken);

        sendKafkaMessage(new KafkaMessage(MessageType.USER_REGISTERED, appUser.getEmail()));
        return token;
    }

    /**
     * CRUD operations
     */
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    @Cacheable(USERS_COLLECTION)
    public List<AppUser> getAllUsers() {
        return appUserRepository.findAll();
    }

    public Optional<AppUser> getUserById(long id) {
        return appUserRepository.findById(id);
    }

    public Optional<AppUser> getUserByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    public String deleteUserById(long id) {
        Optional<AppUser> appUser = appUserRepository.findById(id);
        if (appUser.isPresent() && appUser.get().getAppUserRole() != AppUserRole.ADMIN) {
            appUserRepository.deleteById(id);
            sendKafkaMessage(new KafkaMessage(MessageType.USER_DELETED, appUser.get().getEmail()));
            return String.format("User with id:%s successfully deleted", id);
        } else return String.format("User with id:%s does not exist or is an administrator!", id);
    }

    public String deleteUserByEmail(String email) {
        Optional<AppUser> appUser = appUserRepository.findByEmail(email);
        if (appUser.isPresent() && appUser.get().getAppUserRole() != AppUserRole.ADMIN) {
            appUserRepository.deleteById(appUser.get().getUid());
            sendKafkaMessage(new KafkaMessage(MessageType.USER_DELETED, email));
            return String.format("User with email:%s successfully deleted", email);
        } else return String.format("User with email:%s does not exist or is an administrator!", email);
    }

    public String updateEmailById(long id, String email) {
        boolean isValidEmail = emailValidator
                .test(email);
        if (isValidEmail && appUserRepository.findById(id).isPresent()) {
            appUserRepository.updateEmailById(id, email);
            sendKafkaMessage(new KafkaMessage(MessageType.USER_UPDATED, email));
            return String.format("User email with id:%s successfully updated", id);
        } else throw new IllegalStateException(
                String.format("User with id:%s does not exist or Email is not valid!", id));
    }

    public String updateNameByEmail(String email, String firstName, String lastName) {
        Optional<AppUser> appUser = appUserRepository.findByEmail(email);
        if (appUser.isPresent()) {
            appUserRepository.updateNameByEmail(email, firstName, lastName);
            sendKafkaMessage(new KafkaMessage(MessageType.USER_UPDATED, email));
            return String.format("Username with email:%s successfully updated", email);
        } else throw new IllegalStateException(
                String.format("User with email:%s does not exist or Data is not valid!", email));
    }

    public String updatePasswordByEmail(String email, String password) {
        String encodedPassword = passwordEncoder
                .encode(password);
        Optional<AppUser> appUser = appUserRepository.findByEmail(email);
        if (appUser.isPresent()) {
            appUserRepository.updatePasswordByEmail(email, encodedPassword);
            sendKafkaMessage(new KafkaMessage(MessageType.USER_UPDATED, email));
            return String.format("Password with email:%s successfully updated", email);
        } else throw new IllegalStateException(
                String.format("User with email:%s does not exist or Data is not valid!", email));
    }

    public void enableAppUser(String email) {
        appUserRepository.enableAppUser(email);
        sendKafkaMessage(new KafkaMessage(MessageType.USER_ENABLED, email));
    }

    private void sendKafkaMessage(KafkaMessage command) {
        Gson gson = new Gson();
        String jsonMessage = gson.toJson(command);
        kafkaProducerConfig.initTemplate().send(FROM_CLIENT_TO_MESSAGE_SERVICE_TOPIC, jsonMessage);
    }
}
