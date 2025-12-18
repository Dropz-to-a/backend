package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * accountId 기준 회사 조회
     * - 회사 계정은 accounts 테이블과 1:1 관계
     */
    Optional<Company> findByAccountId(Long accountId);
}
