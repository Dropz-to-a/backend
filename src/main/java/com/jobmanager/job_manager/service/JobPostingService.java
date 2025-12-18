package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.jobposting.JobPostingCreateRequest;
import com.jobmanager.job_manager.dto.jobposting.JobPostingHistoryResponse;
import com.jobmanager.job_manager.dto.jobposting.JobPostingManageResponse;
import com.jobmanager.job_manager.entity.Company;
import com.jobmanager.job_manager.entity.jobposting.EmploymentType;
import com.jobmanager.job_manager.entity.jobposting.JobPosting;
import com.jobmanager.job_manager.entity.jobposting.JobPostingStatus;
import com.jobmanager.job_manager.repository.ApplicationRepository;
import com.jobmanager.job_manager.repository.CompanyRepository;
import com.jobmanager.job_manager.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobPostingService {

    private final JobPostingRepository jobPostingRepository;
    private final ApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;

    /**
     * 채용 공고 등록
     * - DB NOT NULL 컬럼 누락 방지: company_id, title, created_by, currency, status, employment_type
     * - Company PK = accountId 이므로 회사 존재 여부는 findById로 검증
     */
    @Transactional
    public void createPosting(Long accountId, JobPostingCreateRequest request) {

        // 회사 계정 존재 검증 (companies PK = account_id)
        Company company = companyRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("회사 계정을 찾을 수 없습니다."));

        // title NOT NULL 방지 (빈 문자열도 사실상 장애 원인이라 최소 방어)
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("제목(title)은 필수입니다.");
        }

        // employment_type NOT NULL 방지 (요청이 null이면 DB default를 믿지 말고 서버에서 채움)
        EmploymentType employmentType =
                (request.getEmploymentType() != null) ? request.getEmploymentType() : EmploymentType.FULL_TIME;

        JobPosting posting = JobPosting.builder()
                // company_id NOT NULL
                .companyId(company.getAccountId())

                // created_by NOT NULL
                .createdBy(accountId)

                // currency NOT NULL (DB default가 있어도 JPA가 NULL 넣으면 깨짐 → 서버에서 확정)
                .currency("KRW")

                // status NOT NULL (등록 즉시 OPEN)
                .status(JobPostingStatus.OPEN)

                // 요청값
                .title(request.getTitle())
                .description(request.getDescription())
                .employmentType(employmentType)
                .locationText(request.getLocationText())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())

                // 선택값
                .publishedAt(LocalDateTime.now())
                .build();

        jobPostingRepository.save(posting);
    }

    /**
     * 내 공고 목록 조회 (OPEN + CLOSED)
     */
    public List<JobPostingManageResponse> getMyPostings(Long accountId) {

        companyRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("회사 계정을 찾을 수 없습니다."));

        return jobPostingRepository
                .findByCompanyIdAndStatusIn(
                        accountId,
                        List.of(JobPostingStatus.OPEN, JobPostingStatus.CLOSED)
                )
                .stream()
                .map(posting -> JobPostingManageResponse.from(
                        posting,
                        applicationRepository.countByPostingId(posting.getId())
                ))
                .toList();
    }

    /**
     * 공고 기록 조회 (CLOSED)
     */
    public List<JobPostingHistoryResponse> getPostingHistory(Long accountId) {

        companyRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("회사 계정을 찾을 수 없습니다."));

        return jobPostingRepository
                .findByCompanyIdAndStatusOrderByClosedAtDesc(
                        accountId,
                        JobPostingStatus.CLOSED
                )
                .stream()
                .map(posting -> JobPostingHistoryResponse.from(
                        posting,
                        applicationRepository.countByPostingId(posting.getId())
                ))
                .toList();
    }

    /**
     * 공고 삭제 (실제 삭제 X → CLOSED 처리)
     * - 지원자가 1명이라도 있으면 상태 변경하지 않고 에러
     */
    @Transactional
    public void closePosting(Long postingId, Long accountId) {

        companyRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("회사 계정을 찾을 수 없습니다."));

        JobPosting posting = jobPostingRepository
                .findByIdAndCompanyId(postingId, accountId)
                .orElseThrow(() -> new IllegalArgumentException("공고를 찾을 수 없습니다."));

        if (applicationRepository.countByPostingId(postingId) > 0) {
            throw new IllegalStateException("지원자가 존재하는 공고는 삭제할 수 없습니다.");
        }

        posting.setStatus(JobPostingStatus.CLOSED);
        posting.setClosedAt(LocalDateTime.now());
    }
}
