// src/main/java/com/jobmanager/job_manager/dto/company/CompanyProfileUpdateRequest.java
package com.jobmanager.job_manager.dto.company;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyProfileUpdateRequest {

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
}