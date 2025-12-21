package com.jobmanager.job_manager.entity.payment;

public enum PaymentOrderStatus {
    READY,       // 주문 생성됨(결제 전)
    CONFIRMING,  // confirm 요청 처리 중(중복 클릭/재시도 보호)
    PAID,        // 결제 승인 완료
    FAILED,      // 결제 실패
    CANCELLED    // 결제 취소
}
