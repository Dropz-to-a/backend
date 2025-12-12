package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.config.TossProperties;
import com.jobmanager.job_manager.dto.payments.SinglePayRequest;
import com.jobmanager.job_manager.entity.payment.*;
import com.jobmanager.job_manager.repository.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentOrderRepository orderRepo;
    private final TossProperties toss;

    /** 결제 요청 생성(토스 호출 전 단계) */
    public PaymentOrder createOrder(Long companyId, SinglePayRequest req) {

        PaymentOrder order = PaymentOrder.builder()
                .orderId(req.getOrderId())
                .companyId(companyId)
                .employeeId(null)        // 아직 매핑 안함
                .contractId(null)
                .amount(BigDecimal.valueOf(req.getAmount()))
                .status(PaymentOrderStatus.REQUESTED)
                .currency("KRW")
                .method(PaymentMethod.CARD)
                .build();

        return orderRepo.save(order);
    }
}
