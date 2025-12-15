package com.jobmanager.job_manager.dto.payments;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TossKeyInRequest {

    private String orderId;
    private String orderName;

    private int amount;

    private CardInfo card;

    @Getter
    @Builder
    public static class CardInfo {
        private String number;
        private String expirationYear;
        private String expirationMonth;
        private String password;
        private String ownerIdentityNumber;
    }
}
