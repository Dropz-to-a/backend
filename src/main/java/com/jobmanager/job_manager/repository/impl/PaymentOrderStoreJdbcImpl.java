package com.jobmanager.job_manager.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmanager.job_manager.dto.payments.PaymentSnapshot;
import com.jobmanager.job_manager.repository.PaymentOrderStore;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * system_meta(meta_key, meta_value), audit_logs 를 이용해
 * 주문/결제 상태를 저장·조회한다. (스키마 변경 없음)
 */
@Repository
@RequiredArgsConstructor
public class PaymentOrderStoreJdbcImpl implements PaymentOrderStore {

    private final JdbcTemplate jdbc;
    private final ObjectMapper om = new ObjectMapper();

    /* --------- system_meta CRUD (UPSERT) --------- */

    private String k(String orderId, String suffix) {
        return "pay:" + orderId + ":" + suffix;
    }

    private void upsertMeta(String key, String value) {
        // MySQL: meta_key UNIQUE → 있으면 업데이트, 없으면 삽입
        jdbc.update("""
            INSERT INTO system_meta(meta_key, meta_value) VALUES (?, ?)
            ON DUPLICATE KEY UPDATE meta_value = VALUES(meta_value), updated_at = CURRENT_TIMESTAMP
        """, key, value);
    }

    private Optional<String> getMeta(String key) {
        var list = jdbc.query("SELECT meta_value FROM system_meta WHERE meta_key = ?", (rs, i) -> rs.getString(1), key);
        return list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.get(0));
    }

    private boolean existsMetaPrefix(String prefixKey) {
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM system_meta WHERE meta_key LIKE CONCAT(?, '%')", Integer.class, prefixKey);
        return c != null && c > 0;
    }

    /* --------- audit_logs 기록 --------- */

    private void audit(String action, String orderId, Map<String, Object> meta) {
        try {
            String json = om.writeValueAsString(meta != null ? meta : Map.of());
            jdbc.update("""
               INSERT INTO audit_logs(actor_id, action, target_type, target_id, metadata_json)
               VALUES (NULL, ?, 'ORDER', NULL, CAST(? AS JSON))
            """, action + ":" + orderId, json);
        } catch (JsonProcessingException e) {
            // JSON 실패 시 최소 정보만
            jdbc.update("""
               INSERT INTO audit_logs(actor_id, action, target_type, target_id, metadata_json)
               VALUES (NULL, ?, 'ORDER', NULL, NULL)
            """, action + ":" + orderId);
        }
    }

    /* --------- 인터페이스 구현 --------- */

    @Override
    public boolean existsByOrderId(String orderId) {
        return existsMetaPrefix(k(orderId, ""));
    }

    @Override
    public void createPendingOrder(String orderId, Long amount, String orderName, Long accountId) {
        upsertMeta(k(orderId, "amount"), String.valueOf(amount));
        upsertMeta(k(orderId, "status"), "PENDING");
        upsertMeta(k(orderId, "orderName"), orderName != null ? orderName : "");
        if (accountId != null) upsertMeta(k(orderId, "accountId"), String.valueOf(accountId));

        Map<String, Object> meta = new HashMap<>();
        meta.put("amount", amount);
        meta.put("orderName", orderName);
        meta.put("accountId", accountId);
        audit("PAYMENT_ORDER_CREATED", orderId, meta);
    }

    @Override
    public Optional<OrderBrief> findBrief(String orderId) {
        var amount = getMeta(k(orderId, "amount")).map(Long::valueOf).orElse(null);
        var status = getMeta(k(orderId, "status")).orElse(null);
        if (amount == null || status == null) return Optional.empty();
        return Optional.of(new OrderBrief(orderId, amount, status));
    }

    @Override
    public void markPaid(String orderId, String method, String approvedAtIso, String receiptUrl) {
        upsertMeta(k(orderId, "status"), "PAID");
        if (method != null)     upsertMeta(k(orderId, "method"), method);
        if (approvedAtIso != null) upsertMeta(k(orderId, "paidAt"), approvedAtIso);
        if (receiptUrl != null) upsertMeta(k(orderId, "receiptUrl"), receiptUrl);

        Map<String, Object> meta = new HashMap<>();
        meta.put("method", method);
        meta.put("approvedAt", approvedAtIso);
        meta.put("receiptUrl", receiptUrl);
        audit("PAYMENT_APPROVED", orderId, meta);
    }

    @Override
    public void markFailed(String orderId, String failReason) {
        upsertMeta(k(orderId, "status"), "FAILED");
        if (failReason != null) upsertMeta(k(orderId, "failReason"), failReason);

        audit("PAYMENT_FAILED", orderId, Map.of("reason", failReason));
    }

    @Override
    public void markCanceled(String orderId, String canceledAtIso, Long cancelAmount) {
        upsertMeta(k(orderId, "status"), "CANCELED");
        if (canceledAtIso != null) upsertMeta(k(orderId, "canceledAt"), canceledAtIso);
        if (cancelAmount != null)  upsertMeta(k(orderId, "cancelAmount"), String.valueOf(cancelAmount));

        Map<String, Object> meta = new HashMap<>();
        meta.put("canceledAt", canceledAtIso);
        meta.put("cancelAmount", cancelAmount);
        audit("PAYMENT_CANCELED", orderId, meta);
    }

    @Override
    public Optional<String> findPaymentKey(String orderId) {
        return getMeta(k(orderId, "paymentKey"));
    }

    @Override
    public void savePaymentKey(String orderId, String paymentKey) {
        if (paymentKey != null && !paymentKey.isBlank()) {
            upsertMeta(k(orderId, "paymentKey"), paymentKey);
        }
    }

    @Override
    public Optional<PaymentSnapshot> findSnapshot(String orderId) {
        var amount     = getMeta(k(orderId, "amount")).map(Long::valueOf).orElse(null);
        var status     = getMeta(k(orderId, "status")).orElse(null);
        if (amount == null || status == null) return Optional.empty();

        var method     = getMeta(k(orderId, "method")).orElse(null);
        var paidAt     = getMeta(k(orderId, "paidAt")).orElse(null);
        var canceledAt = getMeta(k(orderId, "canceledAt")).orElse(null);
        var receiptUrl = getMeta(k(orderId, "receiptUrl")).orElse(null);
        var pkey       = getMeta(k(orderId, "paymentKey")).orElse(null);

        return Optional.of(PaymentSnapshot.builder()
                .orderId(orderId)
                .amount(amount)
                .status(status)
                .method(method)
                .paidAt(paidAt)
                .canceledAt(canceledAt)
                .receiptUrl(receiptUrl)
                .paymentKey(pkey)
                .build());
    }
}
