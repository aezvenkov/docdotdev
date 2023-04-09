package com.example.clientbackend.organization;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface OrganizationRepository extends ReactiveCrudRepository<Organization, Long> {

    Mono<Organization> findByTitle(String title);

    Mono<Boolean> existsByTitle(String title);

    Mono<Void> updateOrganizationById(long id, Organization organization);

}
