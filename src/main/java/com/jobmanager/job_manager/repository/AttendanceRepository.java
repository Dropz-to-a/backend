package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.UserAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<UserAttendance, Long> {

    /** 특정 직원의 특정 날짜 출퇴근 기록 조회 */
    Optional<UserAttendance> findByAccountIdAndWorkDate(Long accountId, LocalDate workDate);

}

