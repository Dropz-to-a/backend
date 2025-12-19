package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.company.CreateTeamRequest;
import com.jobmanager.job_manager.entity.CompanyTeam;
import com.jobmanager.job_manager.global.exception.errorcodes.CompanyErrorCode;
import com.jobmanager.job_manager.global.exception.exceptions.CompanyException;
import com.jobmanager.job_manager.repository.CompanyRepository;
import com.jobmanager.job_manager.repository.CompanyTeamRepository;
import com.jobmanager.job_manager.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyTeamService {

    private final CompanyTeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyTeam createTeam(Long companyId, CreateTeamRequest req) {

        //회사 온보딩 했는지 확인
        if (!companyRepository.existsByAccountId(companyId)) {
            throw new CompanyException(CompanyErrorCode.COMPANY_NOT_ONBOARDED);
        }

        //회사 부서 중복 확인
        if (teamRepository.existsByCompanyIdAndName(companyId, req.getName())) {
            throw new IllegalArgumentException("이미 존재하는 부서명입니다.");
        }

        CompanyTeam team = CompanyTeam.builder()
                .companyId(companyId)
                .name(req.getName())
                .description(req.getDescription())
                .build();

        return teamRepository.save(team);
    }

    /** 부서 삭제 */
    @Transactional
    public void deleteTeam(Long companyId, Long teamId) {

        CompanyTeam team = teamRepository.findByIdAndCompanyId(teamId, companyId)
                .orElseThrow(() ->
                        new CompanyException(CompanyErrorCode.TEAM_NOT_IN_COMPANY)
                );

        // 삭제한 부서 소속 직원 team_id 는 모두 자동으로 null됨
        employeeRepository.findByCompanyId(companyId)
                .stream()
                .filter(e -> teamId.equals(e.getTeamId()))
                .forEach(e -> e.setTeamId(null));

        teamRepository.delete(team);
    }
}