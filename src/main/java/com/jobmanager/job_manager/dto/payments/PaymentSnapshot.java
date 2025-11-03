package com.jobmanager.job_manager.dto.payments;

import lombok.Builder;
import lombok.Data;

/** 결제 상태 스냅샷 (클라이언트 반환용) */
@Data @Builder
public class PaymentSnapshot {
    private String orderId;
    private Long amount;
    private String status;     // PENDING / PAID / FAILED / CANCELED / PARTIAL_CANCELED
    private String method;     // CARD, VIRTUAL_ACCOUNT, TRANSFER ...
    private String paidAt;     // ISO-8601
    private String canceledAt; // ISO-8601
    private String receiptUrl;
}
