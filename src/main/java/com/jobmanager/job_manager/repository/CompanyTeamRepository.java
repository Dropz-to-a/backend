package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.CompanyTeam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyTeamRepository extends JpaRepository<CompanyTeam, Long> {

    /** 같은 회사 내 부서명 중복 방지 */
    boolean existsByCompanyIdAndName(Long companyId, String name);

    /** 회사 소유 부서인지 검증용 (단건) */
    Optional<CompanyTeam> findByIdAndCompanyId(Long id, Long companyId);

    /** ✅ 내 회사 부서 목록 조회용 */
    List<CompanyTeam> findByCompanyId(Long companyId);
}