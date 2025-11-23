package com.jobmanager.job_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 휴가 정보 (user_leave)
 */
@Entity
@Table(name = "user_leave")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 직원 계정 ID */
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    /** 회사 계정 ID */
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "leave_type", length = 50)
    private String leaveType;

    @Lob
    private String reason;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private Boolean approved;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (approved == null) {
            approved = false;
        }
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
