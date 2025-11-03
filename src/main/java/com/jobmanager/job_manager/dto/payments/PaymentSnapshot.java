package com.jobmanager.job_manager.dto.payments;

import lombok.Builder;
import lombok.Data;

/** 결제 상태 스냅샷 (클라이언트 반환용)
 *  - paymentKey는 취소/정산 로직에서 재사용할 수 있도록 포함
 */
@Data
@Builder
public class PaymentSnapshot {
    private String orderId;
    private Long amount;
    private String status;     // PENDING / PAID / FAILED / CANCELED / PARTIAL_CANCELED
    private String method;     // CARD, VIRTUAL_ACCOUNT, TRANSFER ...
    private String paidAt;     // ISO-8601
    private String canceledAt; // ISO-8601
    private String receiptUrl;

    // ⬇⬇⬇ 추가된 필드 (컴파일 에러 해결 포인트)
    private String paymentKey; // 취소 호출 시 필요할 수 있는 키
}
