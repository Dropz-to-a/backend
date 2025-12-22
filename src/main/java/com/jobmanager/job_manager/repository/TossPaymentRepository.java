package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.payment.TossPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TossPaymentRepository
        extends JpaRepository<TossPayment, byte[]> {

    Optional<TossPayment> findByTossPaymentKey(String tossPaymentKey);
}
