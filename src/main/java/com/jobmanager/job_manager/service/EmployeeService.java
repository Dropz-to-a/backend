package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.entity.Employee;
import com.jobmanager.job_manager.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Employee assignEmployee(Long companyId, Long employeeId) {

        employeeRepository.findByCompanyIdAndEmployeeId(companyId, employeeId)
                .ifPresent(e -> { throw new IllegalArgumentException("이미 이 회사 소속입니다."); });

        Employee employee = Employee.builder()
                .companyId(companyId)
                .employeeId(employeeId)
                .joinedAt(LocalDateTime.now())
                .build();

        return employeeRepository.save(employee);
    }
}
