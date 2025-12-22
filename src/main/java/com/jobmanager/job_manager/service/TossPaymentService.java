package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.payment.*;
import com.jobmanager.job_manager.entity.payment.TossPayment;
import com.jobmanager.job_manager.entity.payment.TossPaymentStatus;
import com.jobmanager.job_manager.repository.TossPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TossPaymentService {

    private final TossPaymentRepository paymentRepository;
    private final TossPaymentClient tossPaymentClient;

    public ConfirmPaymentResponse confirmPayment(
            ConfirmPaymentRequest request
    ) throws Exception {

        HttpResponse<String> response =
                tossPaymentClient.requestConfirm(request);

        if (response.statusCode() != 200) {
            throw new IllegalStateException("결제 승인 실패");
        }

        try {
            TossPayment payment = TossPayment.builder()
                    .paymentId(UUID.randomUUID().toString().getBytes())
                    .tossPaymentKey(request.paymentKey())
                    .tossOrderId(request.orderId())
                    .totalAmount(Long.parseLong(request.amount()))
                    .tossPaymentStatus(TossPaymentStatus.DONE)
                    .requestedAt(LocalDateTime.now())
                    .approvedAt(LocalDateTime.now())
                    .build();

            paymentRepository.save(payment);

            return ConfirmPaymentResponse.builder()
                    .backendOrderId(request.backendOrderId())
                    .tossPaymentStatus(TossPaymentStatus.DONE)
                    .totalAmount(payment.getTotalAmount())
                    .build();

        } catch (Exception e) {
            tossPaymentClient.requestCancel(
                    request.paymentKey(),
                    "DB 오류"
            );
            throw e;
        }
    }

    public ConfirmPaymentResponse getPayment(String backendOrderId) {
        TossPayment payment = paymentRepository
                .findById(backendOrderId.getBytes())
                .orElseThrow();

        return ConfirmPaymentResponse.builder()
                .backendOrderId(backendOrderId)
                .tossPaymentStatus(payment.getTossPaymentStatus())
                .totalAmount(payment.getTotalAmount())
                .build();
    }

    public void cancelPayment(CancelPaymentRequest request)
            throws Exception {

        tossPaymentClient.requestCancel(
                request.paymentKey(),
                request.cancelReason()
        );

        paymentRepository.findByTossPaymentKey(request.paymentKey())
                .ifPresent(p ->
                        p.changeStatus(TossPaymentStatus.CANCELED));
    }
}
