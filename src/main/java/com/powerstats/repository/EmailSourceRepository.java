package com.powerstats.repository;

import com.powerstats.entity.EmailSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailSourceRepository extends JpaRepository<EmailSource, Long> {

    Optional<EmailSource> findByEmailId(String emailId);

    java.util.List<EmailSource> findByStatus(EmailSource.ProcessingStatus status);
}
