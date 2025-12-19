package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.UserFamily;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFamilyRepository extends JpaRepository<UserFamily, Long> {

    List<UserFamily> findByAccountId(Long accountId);

    boolean existsByAccountId(Long accountId);

    boolean existsByAccountIdAndRoleAndName(Long accountId, String role, String name);

    void deleteByAccountId(Long accountId);
}