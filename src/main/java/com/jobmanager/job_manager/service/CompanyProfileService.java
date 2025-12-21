// src/main/java/com/jobmanager/job_manager/service/CompanyProfileService.java
package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.company.CompanyProfileResponse;
import com.jobmanager.job_manager.dto.company.CompanyProfileUpdateRequest;
import com.jobmanager.job_manager.entity.Company;
import com.jobmanager.job_manager.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyProfileService {

    private final CompanyRepository companyRepository;

    @Transactional(readOnly = true)
    public CompanyProfileResponse getMyProfile(Long accountId) {
        Company company = companyRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("회사 프로필이 존재하지 않습니다."));
        return CompanyProfileResponse.from(company);
    }

    public CompanyProfileResponse updateMyProfile(
            Long accountId,
            CompanyProfileUpdateRequest req
    ) {
        Company company = companyRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("회사 프로필이 존재하지 않습니다."));

        if (req.getCompanyName() != null)
            company.setCompanyName(req.getCompanyName());

        if (req.getBusinessNumber() != null)
            company.setBusinessNumber(normalizeBizNo(req.getBusinessNumber()));

        if (req.getZonecode() != null)
            company.setZonecode(req.getZonecode());

        if (req.getAddress() != null)
            company.setAddress(req.getAddress());

        if (req.getDetailAddress() != null)
            company.setDetailAddress(req.getDetailAddress());

        company.setFoundedYear(req.getFoundedYear());
        company.setEmployeeCount(req.getEmployeeCount());
        company.setIndustry(req.getIndustry());
        company.setWebsite(req.getWebsite());
        company.setDescription(req.getDescription());
        company.setCompanyValues(req.getCompanyValues());
        company.setMission(req.getMission());

        return CompanyProfileResponse.from(company);
    }

    private String normalizeBizNo(String raw) {
        return raw == null ? null : raw.replaceAll("[^0-9]", "");
    }

    /** 공개 프로필 조회 (COMPANY) */
    @Transactional(readOnly = true)
    public CompanyProfileResponse getPublicProfile(Long accountId) {

        Company company = companyRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("회사 프로필이 존재하지 않습니다."));

        return CompanyProfileResponse.from(company);
    }
}