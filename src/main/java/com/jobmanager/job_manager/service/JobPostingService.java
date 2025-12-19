package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.jobposting.JobPostingCreateRequest;
import com.jobmanager.job_manager.dto.jobposting.JobPostingHistoryResponse;
import com.jobmanager.job_manager.dto.jobposting.JobPostingManageResponse;
import com.jobmanager.job_manager.dto.jobposting.JobPostingPublicResponse;
import com.jobmanager.job_manager.dto.jobposting.JobPostingUpdateRequest;
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
     * 채용 공고 등록 (회사)
     */
    @Transactional
    public void createPosting(Long accountId, JobPostingCreateRequest request) {

        Company company = companyRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("회사 계정을 찾을 수 없습니다."));

        EmploymentType employmentType =
                request.getEmploymentType() != null
                        ? request.getEmploymentType()
                        : EmploymentType.FULL_TIME;

        JobPosting posting = JobPosting.builder()
                .companyId(company.getAccountId())
                .createdBy(accountId)
                .currency("KRW")
                .status(JobPostingStatus.OPEN)
                .title(request.getTitle())
                .description(request.getDescription())
                .employmentType(employmentType)
                .locationText(request.getLocationText())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
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
     * 공고 수정 (회사)
     */
    @Transactional
    public void updatePosting(
            Long postingId,
            Long accountId,
            JobPostingUpdateRequest request
    ) {
        companyRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("회사 계정을 찾을 수 없습니다."));

        JobPosting posting = jobPostingRepository
                .findByIdAndCompanyId(postingId, accountId)
                .orElseThrow(() ->
                        new IllegalArgumentException("수정할 수 없는 공고입니다.")
                );

        if (posting.getStatus() == JobPostingStatus.CLOSED) {
            throw new IllegalStateException("마감된 공고는 수정할 수 없습니다.");
        }

        if (request.getTitle() != null) {
            posting.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            posting.setDescription(request.getDescription());
        }
        if (request.getEmploymentType() != null) {
            posting.setEmploymentType(request.getEmploymentType());
        }
        if (request.getLocationText() != null) {
            posting.setLocationText(request.getLocationText());
        }
        if (request.getSalaryMin() != null) {
            posting.setSalaryMin(request.getSalaryMin());
        }
        if (request.getSalaryMax() != null) {
            posting.setSalaryMax(request.getSalaryMax());
        }
    }

    /**
     * 공고 삭제 (CLOSED 처리)
     */
    @Transactional
    public void closePosting(Long postingId, Long accountId) {

        companyRepository.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("회사 계정을 찾을 수 없습니다."));

        JobPosting posting = jobPostingRepository
                .findByIdAndCompanyId(postingId, accountId)
                .orElseThrow(() ->
                        new IllegalArgumentException("공고를 찾을 수 없습니다.")
                );

        if (applicationRepository.countByPostingId(postingId) > 0) {
            throw new IllegalStateException("지원자가 존재하는 공고는 삭제할 수 없습니다.");
        }

        posting.setStatus(JobPostingStatus.CLOSED);
        posting.setClosedAt(LocalDateTime.now());
    }

    /**
     * 공개 공고 전체 조회 (지원자/비로그인)
     */
    public List<JobPostingPublicResponse> getPublicPostings() {

        return jobPostingRepository
                .findByStatusOrderByPublishedAtDesc(JobPostingStatus.OPEN)
                .stream()
                .map(posting -> {
                    Company company = companyRepository.findById(posting.getCompanyId())
                            .orElseThrow();
                    return JobPostingPublicResponse.from(
                            posting,
                            company.getCompanyName()
                    );
                })
                .toList();
    }
}
