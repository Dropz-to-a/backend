package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.application.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    long countByPostingId(Long postingId);

    // 중복 지원 방지
    boolean existsByPostingIdAndApplicantId(Long postingId, Long applicantId);

    // USER - 내 지원서 목록
    List<Application> findByApplicantIdOrderByCreatedAtDesc(Long applicantId);

    // COMPANY - 공고별 지원서 목록
    List<Application> findByPostingId(Long postingId);
}