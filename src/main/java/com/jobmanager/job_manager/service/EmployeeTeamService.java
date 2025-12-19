package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.entity.CompanyTeam;
import com.jobmanager.job_manager.entity.Employee;
import com.jobmanager.job_manager.global.exception.errorcodes.CompanyErrorCode;
import com.jobmanager.job_manager.global.exception.exceptions.CompanyException;
import com.jobmanager.job_manager.repository.CompanyTeamRepository;
import com.jobmanager.job_manager.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeTeamService {

    private final EmployeeRepository employeeRepository;
    private final CompanyTeamRepository teamRepository;

    /** 직원 부서 최초 지정 (POST) */
    @Transactional
    public void assignTeam(Long companyId, Long employeeId, Long teamId) {

        Employee employee = employeeRepository
                .findByCompanyIdAndEmployeeId(companyId, employeeId)
                .orElseThrow(() ->
                        new CompanyException(CompanyErrorCode.EMPLOYEE_NOT_IN_COMPANY)
                );

        if (employee.getTeamId() != null) {
            throw new CompanyException(CompanyErrorCode.ALREADY_ASSIGNED_TO_TEAM);
        }

        //부서가 존재하는지 확인
        CompanyTeam team = teamRepository.findById(teamId)
                .orElseThrow(() ->
                        new CompanyException(CompanyErrorCode.TEAM_NOT_FOUND)
                );

        //로그인 된 회사의 부서인지 확인
        if (!team.getCompanyId().equals(companyId)) {
            throw new CompanyException(CompanyErrorCode.TEAM_NOT_IN_COMPANY);
        }

        employee.setTeamId(team.getId());
    }

    /** 직원 부서 변경 (PATCH) */
    @Transactional
    public void changeTeam(Long companyId, Long employeeId, Long teamId) {

        Employee employee = employeeRepository
                .findByCompanyIdAndEmployeeId(companyId, employeeId)
                .orElseThrow(() ->
                        new CompanyException(CompanyErrorCode.EMPLOYEE_NOT_IN_COMPANY)
                );

        if (employee.getTeamId() == null) {
            throw new CompanyException(CompanyErrorCode.NOT_ASSIGNED_TO_TEAM);
        }

        //이미 해당 부서면 막음
        if (employee.getTeamId().equals(teamId)) {
            throw new CompanyException(CompanyErrorCode.ALREADY_IN_THIS_TEAM);
        }

        //부서가 존재하는지 확인
        CompanyTeam team = teamRepository.findById(teamId)
                .orElseThrow(() ->
                        new CompanyException(CompanyErrorCode.TEAM_NOT_FOUND)
                );

        //회사의 부서인지 확인
        if (!team.getCompanyId().equals(companyId)) {
            throw new CompanyException(CompanyErrorCode.TEAM_NOT_IN_COMPANY);
        }

        employee.setTeamId(team.getId());
    }
}