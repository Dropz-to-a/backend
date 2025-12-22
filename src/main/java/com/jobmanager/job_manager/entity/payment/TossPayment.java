package com.jobmanager.job_manager.entity.payment;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "toss_payment")
public class TossPayment {

    /**
     * 결제 자체의 PK
     * UUID → BINARY(16)
     */
    @Id
    @Column(name = "payment_id", nullable = false)
    private byte[] paymentId;

    /**
     * 내부에서 사용하는 주문 식별자
     * (Order 엔티티 없음, 단순 값)
     */
    @Column(name = "order_id", nullable = false)
    private byte[] orderId;

    /**
     * Toss Payments 결제 고유 키
     */
    @Column(name = "toss_payment_key", nullable = false, unique = true)
    private String tossPaymentKey;

    /**
     * 프론트에서 생성한 주문 ID (문자열)
     */
    @Column(name = "toss_order_id", nullable = false)
    private String tossOrderId;

    /**
     * 결제 총 금액
     */
    @Column(name = "total_amount", nullable = false)
    private long totalAmount;

    /**
     * 결제 수단
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "toss_payment_method", nullable = false)
    private TossPaymentMethod tossPaymentMethod;

    /**
     * 결제 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "toss_payment_status", nullable = false)
    private TossPaymentStatus tossPaymentStatus;

    /**
     * 결제 요청 시각
     */
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    /**
     * 결제 승인 시각
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * 결제 상태 변경
     */
    public void changeStatus(TossPaymentStatus status) {
        this.tossPaymentStatus = status;
    }
}
