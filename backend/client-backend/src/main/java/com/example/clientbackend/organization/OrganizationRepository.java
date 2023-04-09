package com.example.clientbackend.organization;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByTitle(String title);

    Boolean existsByTitle(String title);

    @Modifying
    @Transactional
    @Query("UPDATE Organization o SET o.title =:newTitle WHERE o.id =:id")
    void updateTitleById(Long id, String newTitle);
}
