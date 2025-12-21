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

    /** 토스 주문번호 (외부 연동용) */
    @Column(name = "order_id", nullable = false, unique = true, length = 100)
    private String orderId;

    /** 주문명 (결제명) */
    @Column(name = "order_name", nullable = false, length = 200)
    private String orderName;

    /** 결제 주체 (회사 accounts.id) */
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    /** 결제 대상 직원 */
    @Column(name = "employee_id")
    private Long employeeId;

    /** 관련 계약 */
    @Column(name = "contract_id")
    private Long contractId;

    /** 결제 금액 */
    @Column(nullable = false)
    private BigDecimal amount;

    /** 통화 */
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
    @Column(name = "toss_payment_key")
    private String tossPaymentKey;

    /** 토스 응답 원본 */
    @Column(name = "toss_raw_response", columnDefinition = "json")
    private String tossRawResponse;

    /** 시간 정보 */
    private LocalDateTime requestedAt;
    private LocalDateTime paidAt;
    private LocalDateTime cancelledAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (requestedAt == null) requestedAt = LocalDateTime.now();
        if (currency == null) currency = "KRW";
        if (method == null) method = PaymentMethod.CARD;
        if (status == null) status = PaymentOrderStatus.READY;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}