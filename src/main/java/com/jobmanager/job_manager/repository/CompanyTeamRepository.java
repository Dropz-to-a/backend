package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.CompanyTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyTeamRepository extends JpaRepository<CompanyTeam, Long> {

    boolean existsByCompanyIdAndName(Long companyId, String name);

    /** 회사 소유 부서인지 검증용 */
    Optional<CompanyTeam> findByIdAndCompanyId(Long id, Long companyId);
}