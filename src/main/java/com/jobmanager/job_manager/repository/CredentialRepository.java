package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential, Long> {
    Optional<Credential> findByAccountId(Long accountId);
}
