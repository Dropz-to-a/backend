package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(String code); // ROLE_USER, ROLE_COMPANY, ROLE_ADMIN
}
