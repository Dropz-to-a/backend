package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByCompanyIdAndEmployeeId(Long companyId, Long employeeId);

}
