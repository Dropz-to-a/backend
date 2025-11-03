package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {

    // 계정에 부여된 역할 중 가장 먼저 부여된 역할 코드 1건을 바로 조회
    @Query(value = """
            SELECT r.code
            FROM account_roles ar
            JOIN roles r ON r.id = ar.role_id
            WHERE ar.account_id = :accountId
            ORDER BY ar.granted_at ASC
            LIMIT 1
            """, nativeQuery = true)
    Optional<String> findTopRoleCodeByAccountId(Long accountId);
}
