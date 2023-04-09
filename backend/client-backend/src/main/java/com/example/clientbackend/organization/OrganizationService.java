package com.example.clientbackend.organization;

import com.example.clientbackend.config.KafkaProducerConfig;
import com.example.clientbackend.kafka.CommandType;
import com.example.clientbackend.kafka.KafkaCommand;
import com.example.clientbackend.requests.OrganizationRequest;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.example.clientbackend.Constants.FROM_CLIENT_TO_MESSAGE_SERVICE_TOPIC;

@Service
@AllArgsConstructor
@Import(KafkaProducerConfig.class)
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    private final KafkaProducerConfig kafkaProducerConfig;


    public Mono<Organization> getOrganizationById(long id) {
        Mono<Boolean> isExists = organizationRepository.existsById(id);

        return isExists.flatMap(exists -> {
            if (exists) {
                return organizationRepository.findById(id);
            } else {
                return Mono.empty();
            }
        });
    }

    public void createOrganization(OrganizationRequest request) {
        Mono<Boolean> isExists = organizationRepository.existsByTitle(request.title());

        isExists.subscribe(result -> {
            if (result) {
                throw new IllegalStateException("Organization already exists!");
            } else {
                Mono<Organization> organizationMono =
                        organizationRepository.save(Organization.builder().title(request.title()).build());
                organizationMono.subscribe(organization -> {
                    sendKafkaMessage(new KafkaCommand(CommandType.ORGANIZATION_CREATED, organization.getTitle()));
                });
            }
        });
    }

    public void updateOrganization(long id, OrganizationRequest request) {
        Mono<Boolean> isExists = organizationRepository.existsById(id);

        isExists.subscribe(result -> {
            if (result) {
                organizationRepository.updateOrganizationById(
                        id, Organization.builder()
                                .title(request.title())
                                .build()).then(
                        Mono.just(new KafkaCommand(CommandType.ORGANIZATION_UPDATED, "organizationId: " + id))
                ).subscribe(this::sendKafkaMessage);
            } else {
                throw new IllegalStateException("Organization is not exists!");
            }
        });
    }

    public void deleteOrganization(long id) {
        Mono<Boolean> isExists = organizationRepository.existsById(id);

        isExists.subscribe(result -> {
            if (result) {
                organizationRepository.deleteById(id).then(
                        Mono.just(new KafkaCommand(CommandType.ORGANIZATION_DELETED, "organizationId: " + id))
                ).subscribe(this::sendKafkaMessage);
            } else {
                throw new IllegalStateException("Organization is not exists!");
            }
        });
    }

    private void sendKafkaMessage(KafkaCommand command) {
        Gson gson = new Gson();
        String jsonMessage = gson.toJson(command);
        kafkaProducerConfig.initTemplate().send(FROM_CLIENT_TO_MESSAGE_SERVICE_TOPIC, jsonMessage);
    }
}
