package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.dto.business.BusinessExistsRequest;
import com.jobmanager.job_manager.dto.business.BusinessExistsResponse;
import com.jobmanager.job_manager.service.BusinessVerifyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessVerifyController {

    private final BusinessVerifyService service;

    @Tag(
            name = "business-exists",
            description = """
            사업자등록번호의 존재 여부를 조회하는 api
            - nginx에서 우회하여 에이픽 api를 직접 호출함
            """
    )
    @PostMapping("/exists")
    public BusinessExistsResponse exists(
            @RequestBody BusinessExistsRequest request
    ) {
        return service.existsBusiness(request.getBusinessNumber());
    }
}