package com.jobmanager.job_manager.entity;

/** DB 스키마를 건드리지 않기 위해, 우선 상태 Enum만 제공 */
public enum PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    CANCELED,
    PARTIAL_CANCELED
}
