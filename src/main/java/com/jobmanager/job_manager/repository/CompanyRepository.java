package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * 회사 온보딩 여부 확인 (accountId 기준)
     */
    Optional<Company> findByAccountId(Long accountId);

    /**
     * 회사 온보딩 여부 존재 체크
     */
    boolean existsByAccountId(Long accountId);
}