package com.jobmanager.job_manager.entity.jobposting;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_postings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 회사 계정 ID */
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    /** 지점 ID (선택) */
    @Column(name = "branch_id")
    private Long branchId;

    /** 공고 제목 */
    @Column(nullable = false, length = 150)
    private String title;

    /** 공고 설명 */
    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String description;

    /** 고용 형태 */
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false)
    private EmploymentType employmentType;

    /** 근무 위치 */
    @Column(name = "location_text", length = 200)
    private String locationText;

    /** 연봉 최소 */
    @Column(name = "salary_min", precision = 15, scale = 2)
    private BigDecimal salaryMin;

    /** 연봉 최대 */
    @Column(name = "salary_max", precision = 15, scale = 2)
    private BigDecimal salaryMax;

    /** 통화 */
    @Column(length = 3, nullable = false)
    private String currency;

    /** 공고 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobPostingStatus status;

    /** 공개 시점 */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    /** 마감 시점 (삭제 처리 시 사용) */
    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    /** 생성자 (회사 계정 ID) */
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    /** 생성일 */
    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    /** 수정일 */
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;
}
