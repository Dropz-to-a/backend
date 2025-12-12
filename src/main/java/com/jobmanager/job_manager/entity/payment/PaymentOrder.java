package com.jobmanager.job_manager.entity.payment;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 토스 주문번호 (UNIQUE) */
    @Column(name = "order_id", nullable = false, unique = true, length = 100)
    private String orderId;

    /** 결제 주체 (회사 accounts.id) */
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    /** 결제 대상 (직원 accounts.id) */
    @Column(name = "employee_id")
    private Long employeeId;

    /** 관련 계약 (contracts.id) */
    @Column(name = "contract_id")
    private Long contractId;

    /** 결제 금액 */
    @Column(nullable = false)
    private BigDecimal amount;

    /** 통화 (KRW 기본) */
    @Column(nullable = false, length = 3)
    private String currency;

    /** 결제 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentOrderStatus status;

    /** 결제 수단 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    /** 토스 paymentKey */
    @Column(name = "toss_payment_key", length = 200)
    private String tossPaymentKey;

    /** 토스에서 받은 원본 응답(json) */
    @Column(name = "toss_raw_response", columnDefinition = "json")
    private String tossRawResponse;

    /** 결제 요청 시간 */
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    /** 결제 완료 시간 */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /** 결제 취소 시간 */
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    /** 생성 시각 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 수정 시각 (자동 업데이트) */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 엔티티 저장 전 자동 날짜 세팅 */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.requestedAt == null) {
            this.requestedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = PaymentOrderStatus.REQUESTED;
        }
        if (this.currency == null) {
            this.currency = "KRW";
        }
        if (this.method == null) {
            this.method = PaymentMethod.CARD;
        }
    }

    /** 엔티티 업데이트 시 자동 날짜 갱신 */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
