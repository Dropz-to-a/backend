package com.jobmanager.job_manager.repository;

import com.jobmanager.job_manager.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /** 특정 회사의 직원 전체 조회 */
    List<Employee> findByCompanyId(Long companyId);

    /** 회사 + 직원 매핑 확인 */
    Optional<Employee> findByCompanyIdAndEmployeeId(Long companyId, Long employeeId);

    /** 소속 여부 체크용 */
    boolean existsByCompanyIdAndEmployeeId(Long companyId, Long employeeId);

    void deleteByCompanyIdAndEmployeeId(Long companyId, Long employeeId);
}
