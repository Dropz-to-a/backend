package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.UserAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UserAttendanceRepository extends JpaRepository<UserAttendance, Long> {

    /** 특정 회사에서, 특정 직원의 특정 날짜 기록 */
    Optional<UserAttendance> findByAccountIdAndCompanyIdAndWorkDate(
            Long accountId,
            Long companyId,
            LocalDate workDate
    );
}
