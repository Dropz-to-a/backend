package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.company.EmployeeStatusResponse;
import com.jobmanager.job_manager.entity.Company;
import com.jobmanager.job_manager.entity.Employee;
import com.jobmanager.job_manager.repository.CompanyRepository;
import com.jobmanager.job_manager.repository.EmployeeRepository;
import com.jobmanager.job_manager.repository.UserAttendanceRepository;
import com.jobmanager.job_manager.repository.UserLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final UserAttendanceRepository attendanceRepository;
    private final UserLeaveRepository userLeaveRepository;

    /** 회사 정보 조회 */
    public Company getCompany(long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));
    }

    /** 회사 소속 직원 조회 */
    public List<Employee> getMyEmployees(Long companyId) {
        return employeeRepository.findByCompanyId(companyId);
    }

    /** 직원 상태 조회 */
    public List<EmployeeStatusResponse> getEmployeeStatuses(Long companyId) {

        LocalDate today = LocalDate.now();

        List<Employee> employees = employeeRepository.findByCompanyId(companyId);

        return employees.stream().map(emp -> {

            Long empId = emp.getEmployeeId();

            // 1) 휴가 여부
            boolean onLeave = userLeaveRepository
                    .existsByAccountIdAndCompanyIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                            empId, companyId, today, today
                    );

            // 2) 출근 여부
            boolean hasAttendance = attendanceRepository
                    .existsByAccountIdAndWorkDate(empId, today);

            String status;

            if (onLeave) status = "ON_LEAVE";
            else if (hasAttendance) status = "WORKING";
            else status = "NOT_STARTED";

            return new EmployeeStatusResponse(empId, status);

        }).toList();
    }
}
