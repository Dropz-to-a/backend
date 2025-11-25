package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /** 해당 직원이 이미 이 회사 소속인지 확인 (중복 방지) */
    Optional<Employee> findByCompanyIdAndEmployeeId(Long companyId, Long employeeId);
}
