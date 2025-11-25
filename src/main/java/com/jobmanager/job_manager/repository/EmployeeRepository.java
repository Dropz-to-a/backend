package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /** 회사에 특정 직원이 소속되어 있는지 확인 */
    boolean existsByCompanyIdAndEmployeeId(Long companyId, Long employeeId);

    /** 회사 + 직원 조합으로 row 조회 */
    Optional<Employee> findByCompanyIdAndEmployeeId(Long companyId, Long employeeId);
}
