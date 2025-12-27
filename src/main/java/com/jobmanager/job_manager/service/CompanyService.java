package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.company.CompanyEmployeeResponse;
import com.jobmanager.job_manager.dto.company.EmployeeStatusResponse;
import com.jobmanager.job_manager.entity.Company;
import com.jobmanager.job_manager.entity.Employee;
import com.jobmanager.job_manager.entity.UserForm;
import com.jobmanager.job_manager.repository.CompanyRepository;
import com.jobmanager.job_manager.repository.EmployeeRepository;
import com.jobmanager.job_manager.repository.UserAttendanceRepository;
import com.jobmanager.job_manager.repository.UserFormRepository;
import com.jobmanager.job_manager.repository.UserLeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final UserAttendanceRepository attendanceRepository;
    private final UserLeaveRepository userLeaveRepository;
    private final UserFormRepository userFormRepository;

    /** 회사 정보 조회 */
    public Company getCompany(long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("회사를 찾을 수 없습니다."));
    }

    /** 회사 소속 직원 목록 조회 (이름 포함) */
    public List<CompanyEmployeeResponse> getMyEmployees(Long companyId) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return employeeRepository.findByCompanyId(companyId)
                .stream()
                .map(emp -> {

                    Long employeeId = emp.getEmployeeId();

                    String name = userFormRepository.findById(employeeId)
                            .map(UserForm::getName)
                            .orElse(null); // 온보딩 안 된 직원 대비

                    return new CompanyEmployeeResponse(
                            employeeId,
                            name,
                            null, // teamId (미구현)
                            null, // teamName (미구현)
                            emp.getJoinedAt() != null
                                    ? emp.getJoinedAt().format(formatter)
                                    : null
                    );
                })
                .toList();
    }

    /** 직원 상태 조회 (출근 / 휴가 / 미출근) */
    public List<EmployeeStatusResponse> getEmployeeStatuses(Long companyId) {

        LocalDate today = LocalDate.now();

        List<Employee> employees = employeeRepository.findByCompanyId(companyId);

        return employees.stream()
                .map(emp -> {

                    Long employeeId = emp.getEmployeeId();

                    boolean onLeave = userLeaveRepository
                            .existsByAccountIdAndCompanyIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                    employeeId, companyId, today, today
                            );

                    boolean hasAttendance =
                            attendanceRepository.existsByAccountIdAndWorkDate(employeeId, today);

                    String status;
                    if (onLeave) {
                        status = "ON_LEAVE";
                    } else if (hasAttendance) {
                        status = "WORKING";
                    } else {
                        status = "NOT_STARTED";
                    }

                    return new EmployeeStatusResponse(employeeId, status);
                })
                .toList();
    }
}