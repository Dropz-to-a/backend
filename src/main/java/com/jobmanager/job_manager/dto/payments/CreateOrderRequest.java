package com.jobmanager.job_manager.dto.payments;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
import java.util.Map;

/** 주문 생성 요청 (선택: 아이템/메타 – 로깅/정산용) */
@Data
public class CreateOrderRequest {
    @NotNull @Min(1)
    private Long totalAmount;

    @NotBlank
    private String orderName;                 // 예: "상품명 외 1건"

    private List<Item> items;                 // 선택
    private Map<String, Object> orderMeta;    // 선택

    @Data
    public static class Item {
        @NotBlank private String sku;
        @NotBlank private String name;
        @NotNull @Min(0) private Long unitPrice;
        @NotNull @Min(1) private Integer qty;
    }
}
