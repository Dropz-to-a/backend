package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.UserLeave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserLeaveRepository extends JpaRepository<UserLeave, Long> {

    List<UserLeave> findByCompanyIdAndAccountId(Long companyId, Long accountId);

    /** 오늘 휴가인지 검사 */
    boolean existsByAccountIdAndCompanyIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long accountId,
            Long companyId,
            LocalDate today1,
            LocalDate today2
    );
}
