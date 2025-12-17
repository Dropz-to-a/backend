package com.jobmanager.job_manager.entity.jobposting;

public enum JobPostingStatus {

    DRAFT,      // 작성 중
    OPEN,       // 모집 중
    CLOSED,     // 모집 종료 (삭제 처리)
    ARCHIVED    // 보관 (이번 단계에서는 미사용)

}
