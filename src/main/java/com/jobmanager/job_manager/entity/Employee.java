package com.jobmanager.job_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 회사 account_id */
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    /** 직원 account_id */
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    /** 부서(team) ID (NULL 가능) */
    @Column(name = "team_id")
    private Long teamId;

    /** 직원이 회사에 소속된 날짜 */
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
}