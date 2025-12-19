// src/main/java/com/jobmanager/job_manager/dto/company/CompanyProfileResponse.java
package com.jobmanager.job_manager.dto.company;

import com.jobmanager.job_manager.entity.Company;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CompanyProfileResponse {

    private String companyName;
    private String businessNumber;

    private String zonecode;
    private String address;
    private String detailAddress;

    private Integer foundedYear;
    private Integer employeeCount;
    private String industry;
    private String website;

    private String description;
    private String companyValues;
    private String mission;

    public static CompanyProfileResponse from(Company c) {
        return CompanyProfileResponse.builder()
                .companyName(c.getCompanyName())
                .businessNumber(c.getBusinessNumber())
                .zonecode(c.getZonecode())
                .address(c.getAddress())
                .detailAddress(c.getDetailAddress())
                .foundedYear(c.getFoundedYear())
                .employeeCount(c.getEmployeeCount())
                .industry(c.getIndustry())
                .website(c.getWebsite())
                .description(c.getDescription())
                .companyValues(c.getCompanyValues())
                .mission(c.getMission())
                .build();
    }
}