package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.application.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /** 공고별 지원자 수 */
    int countByPostingId(Long postingId);
}
