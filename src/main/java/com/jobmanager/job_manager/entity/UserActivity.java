package com.jobmanager.job_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    /** 맡은 직업 */
    @Column(name = "user_position", nullable = false)
    private String userPosition;

    /** 회사 이름 */
    @Column(name = "company_name", nullable = false)
    private String companyName;

    /** 한 내용 */
    @Lob
    @Column(nullable = false)
    private String description;

    /** 시작 날짜 */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /** 마지막 날짜 (null = 현재 재직중) */
    @Column(name = "end_date")
    private LocalDate endDate;
}