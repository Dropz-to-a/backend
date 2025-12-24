package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.company.CompanyEmployeeResponse;
import com.jobmanager.job_manager.dto.company.CompanyTeamResponse;
import com.jobmanager.job_manager.entity.CompanyTeam;
import com.jobmanager.job_manager.entity.Employee;
import com.jobmanager.job_manager.repository.CompanyTeamRepository;
import com.jobmanager.job_manager.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyEmployeeQueryService {

    private final EmployeeRepository employeeRepository;
    private final CompanyTeamRepository teamRepository;

    public List<CompanyEmployeeResponse> getMyCompanyEmployees(Long companyId) {

        List<Employee> employees = employeeRepository.findByCompanyId(companyId);

        // teamId → teamName 매핑
        Map<Long, String> teamMap = teamRepository.findByCompanyId(companyId)
                .stream()
                .collect(Collectors.toMap(
                        CompanyTeam::getId,
                        CompanyTeam::getName
                ));

        return employees.stream()
                .map(e -> new CompanyEmployeeResponse(
                        e.getEmployeeId(),
                        e.getTeamId(),
                        e.getTeamId() != null ? teamMap.get(e.getTeamId()) : null,
                        e.getJoinedAt().toString()
                ))
                .toList();
    }

    public List<CompanyTeamResponse> getMyTeams(Long companyId) {
        return teamRepository.findByCompanyId(companyId)
                .stream()
                .map(team -> new CompanyTeamResponse(
                        team.getId(),
                        team.getName(),
                        team.getDescription()
                ))
                .toList();
    }
}