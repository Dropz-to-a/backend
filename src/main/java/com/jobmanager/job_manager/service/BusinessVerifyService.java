package com.jobmanager.job_manager.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobmanager.job_manager.dto.business.BusinessExistsResponse;
import com.jobmanager.job_manager.global.config.ApickProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class BusinessVerifyService {

    private final RestClient restClient;
    private final ApickProperties props;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BusinessExistsResponse existsBusiness(String businessNumber) {

        String bizNo = normalizeBizNo(businessNumber);
        String url = props.getBaseUrl() + "/biz_detail";

        System.out.println("[APICK] url=" + url);
        System.out.println("[APICK] authKey.length=" +
                (props.getAuthKey() == null ? "null" : props.getAuthKey().length()));
        System.out.println("[APICK] biz_no=" + bizNo);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("biz_no", bizNo);

        try {
            String raw = restClient.post()
                    .uri(url)
                    .header("CL_AUTH_KEY", props.getAuthKey())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            System.out.println("[APICK] raw=" + raw);

            if (raw == null || raw.isBlank()) {
                System.out.println("[APICK] raw is blank");
                return new BusinessExistsResponse(false, null);
            }

            JsonNode root = objectMapper.readTree(raw);
            JsonNode dataNode = root.path("data");

            int success = dataNode.path("success").asInt(0);
            String companyName = dataNode.path("회사명").asText(null);

            System.out.println("[APICK] parsed success=" + success);
            System.out.println("[APICK] parsed companyName=" + companyName);

            if (success == 1) {
                return new BusinessExistsResponse(true, companyName);
            }

            return new BusinessExistsResponse(false, null);

        } catch (HttpClientErrorException e) {
            System.out.println("[APICK] HTTP ERROR status=" + e.getStatusCode());
            System.out.println("[APICK] HTTP ERROR body=" + e.getResponseBodyAsString());
            return new BusinessExistsResponse(false, null);
        } catch (Exception e) {
            System.out.println("[APICK] EXCEPTION=" + e.getMessage());
            return new BusinessExistsResponse(false, null);
        }
    }

    private String normalizeBizNo(String bizNo) {
        if (bizNo == null) return "";
        return bizNo
                .replace("/", "")
                .replace("-", "")
                .trim();
    }
}