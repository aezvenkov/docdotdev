package com.example.clientbackend.appuser;

import com.example.clientbackend.appuser.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE AppUser a SET a.enabled = TRUE WHERE a.email = ?1")
    int enableAppUser(String email);

    @Transactional
    @Modifying
    @Query("UPDATE AppUser a SET a.email =:newEmail WHERE a.uid =:uid")
    void updateEmailById(@Param("uid") Long uid, @Param("newEmail") String newEmail);

    @Transactional
    @Modifying
    @Query("UPDATE AppUser a SET a.firstName =:firstName, a.lastName =:lastName WHERE a.email =:email")
    void updateNameByEmail(@Param("email") String email, @Param("firstName") String firstName,
                           @Param("lastName") String lastName);

    @Transactional
    @Modifying
    @Query("UPDATE AppUser a SET a.password =:newPassword WHERE a.email =:email")
    void updatePasswordByEmail(@Param("email") String email, @Param("newPassword") String newPassword);

    @Transactional
    @Modifying
    @Query("DELETE FROM AppUser WHERE enabled = FALSE")
    void deleteDisabledAppUsers();
}
