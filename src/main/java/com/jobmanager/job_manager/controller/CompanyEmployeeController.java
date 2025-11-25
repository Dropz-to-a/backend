package com.jobmanager.job_manager.controller;

import com.jobmanager.job_manager.entity.Employee;
import com.jobmanager.job_manager.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyEmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/assign-employee")
    public Employee assign(
            @RequestParam Long companyId,
            @RequestParam Long employeeId
    ) {
        return employeeService.assignEmployee(companyId, employeeId);
    }
}
