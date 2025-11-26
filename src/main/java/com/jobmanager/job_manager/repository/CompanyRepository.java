// src/main/java/com/jobmanager/job_manager/repository/CompanyRepository.java
package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}