package com.jobmanager.job_manager.dto.payments;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class CheckoutInfoResponse {
    private String clientKey;      // 프론트 위젯에서 사용
    private String orderId;
    private Long amount;
    private String orderName;
    private Customer customer;

    @Data @Builder
    public static class Customer {
        private String name;
        private String email;
    }
}
