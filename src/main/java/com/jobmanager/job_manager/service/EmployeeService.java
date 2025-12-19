package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.entity.Account;
import com.jobmanager.job_manager.entity.Employee;
import com.jobmanager.job_manager.global.exception.errorcodes.CompanyErrorCode;
import com.jobmanager.job_manager.global.exception.exceptions.CompanyException;
import com.jobmanager.job_manager.repository.AccountRepository;
import com.jobmanager.job_manager.repository.CompanyRepository;
import com.jobmanager.job_manager.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;
    private final CompanyRepository companyRepository;

    /** 회사가 직원 등록 */
    public Employee assignEmployee(Long companyId, Long employeeId) {

        // 0️⃣ 회사 온보딩 완료 여부 확인
        if (!companyRepository.existsByAccountId(companyId)) {
            throw new CompanyException(CompanyErrorCode.COMPANY_NOT_ONBOARDED);
        }

        // 1️⃣ 직원 계정 실존 여부 확인
        Account account = accountRepository.findById(employeeId)
                .orElseThrow(() ->
                        new CompanyException(CompanyErrorCode.EMPLOYEE_NOT_FOUND)
                );

        // 2️⃣ USER 계정만 직원으로 등록 가능
        if (account.getAccountType() != Account.AccountType.USER) {
            throw new CompanyException(CompanyErrorCode.INVALID_EMPLOYEE_ACCOUNT);
        }

        // 🔥 3️⃣ 이미 우리 회사 직원인지 먼저 체크
        employeeRepository.findByCompanyIdAndEmployeeId(companyId, employeeId)
                .ifPresent(e -> {
                    throw new CompanyException(
                            CompanyErrorCode.ALREADY_EMPLOYEE_OF_COMPANY
                    );
                });

        // 🔥 4️⃣ 다른 회사 소속 여부 체크
        employeeRepository.findByEmployeeId(employeeId)
                .ifPresent(e -> {
                    throw new CompanyException(
                            CompanyErrorCode.EMPLOYEE_ALREADY_ASSIGNED
                    );
                });

        Employee employee = Employee.builder()
                .companyId(companyId)
                .employeeId(employeeId)
                .joinedAt(LocalDateTime.now())
                .build();

        return employeeRepository.save(employee);
    }

    /** 회사가 직원 등록 해제 */
    public void removeEmployee(Long companyId, Long employeeId) {

        Employee emp = employeeRepository
                .findByCompanyIdAndEmployeeId(companyId, employeeId)
                .orElseThrow(() ->
                        new CompanyException(CompanyErrorCode.EMPLOYEE_NOT_IN_COMPANY)
                );

        employeeRepository.delete(emp);
    }
}