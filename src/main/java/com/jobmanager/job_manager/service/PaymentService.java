package com.jobmanager.job_manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmanager.job_manager.config.TossProperties;
import com.jobmanager.job_manager.dto.payments.SinglePayRequest;
import com.jobmanager.job_manager.dto.payments.TossKeyInRequest;
import com.jobmanager.job_manager.dto.payments.TossPayResponse;
import com.jobmanager.job_manager.entity.payment.*;
import com.jobmanager.job_manager.repository.PaymentOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentOrderRepository orderRepo;
    private final TossProperties toss;
    private final ObjectMapper om = new ObjectMapper();

    /** STEP 1 — DB에 주문 생성 */
    @Transactional
    public PaymentOrder createOrder(Long companyId, SinglePayRequest req) {

        PaymentOrder order = PaymentOrder.builder()
                .orderId(req.getOrderId())
                .companyId(companyId)
                .employeeId(null)
                .contractId(null)
                .currency("KRW")
                .status(PaymentOrderStatus.REQUESTED)
                .method(PaymentMethod.CARD)
                .amount(BigDecimal.valueOf(req.getAmount()))
                .build();

        return orderRepo.save(order);
    }

    /** STEP 2 — Tosspayments Key-in 결제 요청 */
    @Transactional
    public TossPayResponse requestPay(Long companyId, SinglePayRequest req) throws Exception {

        // 1) DB 저장
        PaymentOrder order = createOrder(companyId, req);

        // 카드 유효기간 분리
        String[] exp = req.getCardExpiration().split("/");
        String expMonth = exp[0];
        String expYear = exp[1].length() == 2 ? "20" + exp[1] : exp[1];

        // 2) Toss 결제 요청 JSON 생성
        TossKeyInRequest tossReq = TossKeyInRequest.builder()
                .orderId(req.getOrderId())
                .orderName(req.getOrderName())
                .amount(req.getAmount())
                .card(TossKeyInRequest.CardInfo.builder()
                        .number(req.getCardNumber())
                        .expirationMonth(expMonth)
                        .expirationYear(expYear)
                        .password(req.getCardPassword())
                        .ownerIdentityNumber(req.getCustomerIdentityNumber())
                        .build())
                .build();

        // 3) HTTP 요청 헤더
        RestTemplate rt = new RestTemplate();

        String secretKey = toss.getSecretKey();
        String encoded = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encoded);

        HttpEntity<String> entity =
                new HttpEntity<>(om.writeValueAsString(tossReq), headers);

        // 4) Toss API 호출
        String url = toss.getBaseUrl() + "/v1/payments/key-in";

        ResponseEntity<TossPayResponse> resp;

        try {
            resp = rt.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    TossPayResponse.class
            );
        } catch (Exception e) {

            order.setStatus(PaymentOrderStatus.FAILED);
            orderRepo.save(order);

            log.error("TossPay Error = {}", e.getMessage());
            throw e;
        }

        // 5) 성공 처리
        TossPayResponse body = resp.getBody();

        order.setStatus(PaymentOrderStatus.PAID);
        orderRepo.save(order);

        return body;
    }
}
