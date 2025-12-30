package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.application.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // 공고별 지원자 수
    long countByPostingId(Long postingId);

    // 중복 지원 방지
    boolean existsByPostingIdAndWriterId(Long postingId, Long writerId);

    // USER - 내 지원서 목록
    List<Application> findByWriterIdOrderByCreatedAtDesc(Long writerId);

    // 본인 지원서 상세 조회 (USER)
    Optional<Application> findByIdAndWriterId(Long id, Long writerId);

    // COMPANY - 공고별 지원서 목록
    List<Application> findByPostingId(Long postingId);
}