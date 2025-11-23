package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.UserLeave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserLeaveRepository extends JpaRepository<UserLeave, Long> {

    /** 회사 기준 직원 휴가 목록 */
    List<UserLeave> findByCompanyIdAndAccountId(Long companyId, Long accountId);
}
