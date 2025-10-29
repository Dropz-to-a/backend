package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRoleRepository extends JpaRepository<AccountRole, AccountRole.PK> { }
