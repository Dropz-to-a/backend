package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.jobposting.JobPosting;
import com.jobmanager.job_manager.entity.jobposting.JobPostingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    /** 내 공고 관리 (모집중 + 마감) */
    List<JobPosting> findByCompanyIdAndStatusIn(
            Long companyId,
            List<JobPostingStatus> statuses
    );

    /** 공고 기록 (마감된 공고) */
    List<JobPosting> findByCompanyIdAndStatusOrderByClosedAtDesc(
            Long companyId,
            JobPostingStatus status
    );

    /** 회사 소유 공고 단건 조회 (권한 검증용) */
    Optional<JobPosting> findByIdAndCompanyId(
            Long id,
            Long companyId
    );

    List<JobPosting> findByStatusOrderByPublishedAtDesc(JobPostingStatus status);
}
