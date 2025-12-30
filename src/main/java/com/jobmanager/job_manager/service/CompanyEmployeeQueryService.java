package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.company.CompanyEmployeeResponse;
import com.jobmanager.job_manager.dto.company.CompanyTeamResponse;
import com.jobmanager.job_manager.entity.CompanyTeam;
import com.jobmanager.job_manager.entity.Employee;
import com.jobmanager.job_manager.entity.UserForm;
import com.jobmanager.job_manager.entity.Account;
import com.jobmanager.job_manager.entity.enums.EmploymentStatus;
import com.jobmanager.job_manager.repository.CompanyTeamRepository;
import com.jobmanager.job_manager.repository.EmployeeRepository;
import com.jobmanager.job_manager.repository.UserFormRepository;
import com.jobmanager.job_manager.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyEmployeeQueryService {

    private final EmployeeRepository employeeRepository;
    private final CompanyTeamRepository teamRepository;
    private final UserFormRepository userFormRepository;

    // 기존 코드 유지 + 이미 추가돼 있던 레포지토리
    private final AccountRepository accountRepository;

    public List<CompanyEmployeeResponse> getMyCompanyEmployees(Long companyId) {

        List<Employee> employees = employeeRepository.findByCompanyId(companyId);

        // teamId → teamName 매핑
        Map<Long, String> teamMap = teamRepository.findByCompanyId(companyId)
                .stream()
                .collect(Collectors.toMap(
                        CompanyTeam::getId,
                        CompanyTeam::getName
                ));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return employees.stream()
                .map(e -> {

                    Long employeeId = e.getEmployeeId();

                    // 직원 이름 조회 (user_form)
                    String name = userFormRepository.findById(employeeId)
                            .map(UserForm::getName)
                            .orElse(null); // 온보딩 안 된 직원 대비

                    Long teamId = e.getTeamId();
                    String teamName = teamId != null ? teamMap.get(teamId) : null;

                    return new CompanyEmployeeResponse(
                            employeeId,                           // employeeAccountId
                            name,                                 // name
                            teamId,                               // teamId
                            teamName,                             // teamName
                            e.getJoinedAt() != null               // joinedAt
                                    ? e.getJoinedAt().format(formatter)
                                    : null
                    );
                })
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

    // 직원 등록 + 유저 재직 상태 변경
    @Transactional
    public void registerEmployee(Long companyId, Long employeeId) {

        // new Employee(companyId, employeeId)
        // 팩토리 메서드 사용 (에러 원인 해결)
        employeeRepository.save(
                Employee.create(companyId, employeeId)
        );

        Account user = accountRepository.findById(employeeId).get();
        user.setEmploymentStatus(EmploymentStatus.EMPLOYED);
    }
}
