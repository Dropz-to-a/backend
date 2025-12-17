package com.jobmanager.job_manager.entity.application;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 지원한 공고 ID */
    @Column(name = "posting_id", nullable = false)
    private Long postingId;

    /** 지원자 계정 ID */
    @Column(name = "applicant_id", nullable = false)
    private Long applicantId;

    /** 지원 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    /** 사용한 이력서 ID (선택) */
    @Column(name = "resume_id")
    private Long resumeId;

    /** 커버레터 (선택) */
    @Lob
    @Column(name = "cover_letter", columnDefinition = "MEDIUMTEXT")
    private String coverLetter;

    /** 기업 내부 메모 */
    @Column(name = "memo_internal", length = 300)
    private String memoInternal;

    /** 지원 일시 */
    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    /** 수정 일시 */
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;
}
