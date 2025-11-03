package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.dto.payments.PaymentSnapshot;

import java.util.Optional;

/**
 * 결제/주문 저장소 포트.
 * - 지금은 JPA를 상속하지 않는다. (네 덤프 테이블 이름에 맞춰 구현체를 별도로 붙일 예정)
 * - 다음 단계에서 실제 테이블/컬럼명에 맞춰 JdbcTemplate/JPA/MyBatis 중 택1로 구현.
 */
public interface PaymentOrderStore {

    boolean existsByOrderId(String orderId);

    /** 주문 PENDING 생성 */
    void createPendingOrder(String orderId, Long amount, String orderName, Long accountId);

    /** 상태/금액 검증용 요약 조회 */
    Optional<OrderBrief> findBrief(String orderId);

    /** 결제 완료 반영 */
    void markPaid(String orderId, String method, String approvedAtIso, String receiptUrl);

    /** 실패 반영(필요 시) */
    void markFailed(String orderId, String failReason);

    /** 취소 반영 */
    void markCanceled(String orderId, String canceledAtIso, Long cancelAmount);

    /** 결제키 저장/조회가 가능한 경우 사용(취소 호출 시 필요) */
    Optional<String> findPaymentKey(String orderId);
    void savePaymentKey(String orderId, String paymentKey);

    /** 최신 스냅샷 조회(클라이언트 응답용) */
    Optional<PaymentSnapshot> findSnapshot(String orderId);

    /** 간단 요약 */
    record OrderBrief(String orderId, Long amount, String status) {}
}
