package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.payment.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    Optional<PaymentOrder> findByOrderId(String orderId);
    boolean existsByOrderId(String orderId);
}