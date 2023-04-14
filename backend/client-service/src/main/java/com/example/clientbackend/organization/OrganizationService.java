package com.example.clientbackend.organization;

import com.example.clientbackend.config.KafkaProducerConfig;
import com.example.clientbackend.kafka.CommandType;
import com.example.clientbackend.kafka.KafkaCommand;
import com.example.clientbackend.requests.OrganizationRequest;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import static com.example.clientbackend.Constants.FROM_CLIENT_TO_MESSAGE_SERVICE_TOPIC;

@Service
@AllArgsConstructor
@Import(KafkaProducerConfig.class)
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    private final KafkaProducerConfig kafkaProducerConfig;


    public Organization getOrganizationById(long id) {
        boolean isExists = organizationRepository.existsById(id);

        if (isExists) {
            return organizationRepository.findById(id).get();
        } else {
            return null;
        }
    }

    public void createOrganization(OrganizationRequest request) {
        boolean isExists = organizationRepository.existsByTitle(request.title());

        if (isExists) {
            throw new IllegalStateException("Organization already exists!");
        } else {
            Organization organization = organizationRepository.save(Organization.builder().title(request.title()).build());
            sendKafkaMessage(new KafkaCommand(CommandType.ORGANIZATION_CREATED, organization.getTitle()));
        }
    }

    public void updateOrganization(long id, OrganizationRequest request) {
        boolean isExists = organizationRepository.existsById(id);

        if (isExists) {
            organizationRepository.updateTitleById(id, request.title());
            sendKafkaMessage(new KafkaCommand(CommandType.ORGANIZATION_UPDATED, "organizationId: " + id));
        } else {
            throw new IllegalStateException("Organization is not exists!");
        }
    }

    public void deleteOrganization(long id) {
        boolean isExists = organizationRepository.existsById(id);

        if (isExists) {
            organizationRepository.deleteById(id);
            sendKafkaMessage(new KafkaCommand(CommandType.ORGANIZATION_DELETED, "organizationId: " + id));
        } else {
            throw new IllegalStateException("Organization is not exists!");
        }
    }

    private void sendKafkaMessage(KafkaCommand command) {
        Gson gson = new Gson();
        String jsonMessage = gson.toJson(command);
        kafkaProducerConfig.initTemplate().send(FROM_CLIENT_TO_MESSAGE_SERVICE_TOPIC, jsonMessage);
    }
}
