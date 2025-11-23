package com.jobmanager.job_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 출근/퇴근 기록 테이블 매핑 (user_attendance)
 * company_id 는 회사 계정의 accounts.id
 * account_id 는 직원(유저) 계정의 accounts.id
 */
@Entity
@Table(name = "user_attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 직원 계정 ID (accounts.id) */
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    /** 회사 계정 ID (accounts.id) */
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    /** 근무 날짜 (yyyy-MM-dd 기준) */
    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    /** 출근 시간 */
    @Column(name = "clock_in")
    private LocalDateTime clockIn;

    /** 퇴근 시간 */
    @Column(name = "clock_out")
    private LocalDateTime clockOut;

    /** 상태: WORK / LATE / ABSENT / VACATION 등 문자열로 저장 */
    @Column(name = "status", length = 20)
    private String status;
}
