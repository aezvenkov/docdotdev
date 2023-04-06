package com.example.clientbackend.appuser;

import com.example.clientbackend.appuser.model.AppUser;
import com.example.clientbackend.appuser.model.AppUserRole;
import com.example.clientbackend.config.KafkaProducerConfig;
import com.example.clientbackend.email.EmailValidator;
import com.example.clientbackend.kafka.CommandType;
import com.example.clientbackend.kafka.KafkaCommand;
import com.example.clientbackend.requests.AuthenticationRequest;
import com.example.clientbackend.requests.AuthenticationResponse;
import com.example.clientbackend.requests.RegistrationRequest;
import com.example.clientbackend.token.TokenType;
import com.example.clientbackend.token.confirmation.ConfirmationToken;
import com.example.clientbackend.token.confirmation.ConfirmationTokenService;
import com.example.clientbackend.token.jwt.JwtService;
import com.example.clientbackend.token.jwt.JwtToken;
import com.example.clientbackend.token.jwt.JwtTokenRepository;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.time.LocalDateTime;

import static com.example.clientbackend.Constants.*;

@Service
@AllArgsConstructor
@EnableScheduling
@Import(KafkaProducerConfig.class)
public class AuthenticationService {

    private final AppUserService appUserService;

    private final ConfirmationTokenService confirmationTokenService;

    private final JwtService jwtService;

    private EmailValidator emailValidator;

    private final JwtTokenRepository jwtTokenRepositoryRepository;

    private final KafkaProducerConfig kafkaProducerConfig;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;


    private void saveUserJwtToken(AppUser user, String jwtToken) {
        var token = JwtToken.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        jwtTokenRepositoryRepository.save(token);
    }

    private void revokeAllUserTokens(AppUser user) {
        var validUserTokens = jwtTokenRepositoryRepository.findAllValidTokenByUser(user.getUid());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        jwtTokenRepositoryRepository.saveAll(validUserTokens);
    }

    public AuthenticationResponse register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator
                .test(request.email());
        if (!isValidEmail) {
            throw new IllegalStateException("Email not valid!");
        }
        var user = new AppUser(request.firstName(), request.lastName(), request.email(), request.password(), AppUserRole.USER);

        String confirmationToken = appUserService.signUser(user);
        //emailSender.send(request.email(), buildEmail(request.firstName(), buildConfirmLink(confirmationToken)));

        var jwtToken = jwtService.generateToken(user);
        saveUserJwtToken(user, jwtToken);

        return new AuthenticationResponse(confirmationToken, jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var user = appUserService.getUserByEmail(request.email()).orElseThrow();

        if (user.isEnabled()) {
            var jwtToken = jwtService.generateToken(user);
            revokeAllUserTokens(user);
            saveUserJwtToken(user, jwtToken);

            sendKafkaMessage(new KafkaCommand(CommandType.USER_AUTHORISED, user.getEmail()));
            return new AuthenticationResponse(null, jwtToken);
        } else return null;
    }

    @Transactional
    public Mono<String> confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() -> new IllegalStateException("Token not found!"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Email already confirmed!");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Token expired!");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(confirmationToken.getAppUser().getEmail());
        sendKafkaMessage(new KafkaCommand(CommandType.USER_ENABLED, confirmationToken.getAppUser().getEmail()));

        return Mono.just("confirmed");
    }

    private String buildConfirmLink(String token) {
        String hostAddress = InetAddress.getLoopbackAddress().getHostAddress();
        return String.format(TOKEN_CONFIRM_LINK_FORMAT, hostAddress, SERVICE_PORT, token);
    }

    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

    private void sendKafkaMessage(KafkaCommand command) {
        Gson gson = new Gson();
        String jsonMessage = gson.toJson(command);
        kafkaProducerConfig.initTemplate().send(FROM_CLIENT_TO_MESSAGE_SERVICE_TOPIC, jsonMessage);
    }
}
