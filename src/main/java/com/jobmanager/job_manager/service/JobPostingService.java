package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.jobposting.JobPostingCreateRequest;
import com.jobmanager.job_manager.dto.jobposting.JobPostingHistoryResponse;
import com.jobmanager.job_manager.dto.jobposting.JobPostingManageResponse;
import com.jobmanager.job_manager.entity.jobposting.JobPosting;
import com.jobmanager.job_manager.entity.jobposting.JobPostingStatus;
import com.jobmanager.job_manager.repository.ApplicationRepository;
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

    /**
     * 채용 공고 등록
     */
    @Transactional
    public void createPosting(
            Long companyId,
            JobPostingCreateRequest request
    ) {
        JobPosting posting = JobPosting.builder()
                .companyId(companyId)
                .title(request.getTitle())
                .description(request.getDescription())
                .employmentType(request.getEmploymentType())
                .locationText(request.getLocationText())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .currency("KRW")
                .status(JobPostingStatus.OPEN)
                .publishedAt(LocalDateTime.now())
                .createdBy(companyId)
                .build();

        jobPostingRepository.save(posting);
    }

    /**
     * 내 공고 관리 목록 조회
     */
    public List<JobPostingManageResponse> getMyPostings(Long companyId) {
        return jobPostingRepository
                .findByCompanyIdAndStatusIn(
                        companyId,
                        List.of(JobPostingStatus.OPEN, JobPostingStatus.CLOSED)
                )
                .stream()
                .map(p -> JobPostingManageResponse.from(
                        p,
                        applicationRepository.countByPostingId(p.getId())
                ))
                .toList();
    }

    /**
     * 공고 기록 조회
     */
    public List<JobPostingHistoryResponse> getPostingHistory(Long companyId) {
        return jobPostingRepository
                .findByCompanyIdAndStatusOrderByClosedAtDesc(
                        companyId,
                        JobPostingStatus.CLOSED
                )
                .stream()
                .map(p -> JobPostingHistoryResponse.from(
                        p,
                        applicationRepository.countByPostingId(p.getId())
                ))
                .toList();
    }

    /**
     * 공고 삭제 (CLOSED 처리)
     */
    @Transactional
    public void closePosting(Long postingId, Long companyId) {

        JobPosting posting = jobPostingRepository
                .findByIdAndCompanyId(postingId, companyId)
                .orElseThrow(() ->
                        new IllegalArgumentException("공고를 찾을 수 없습니다.")
                );

        if (applicationRepository.countByPostingId(postingId) > 0) {
            throw new IllegalStateException(
                    "지원자가 존재하는 공고는 삭제할 수 없습니다."
            );
        }

        posting.setStatus(JobPostingStatus.CLOSED);
        posting.setClosedAt(LocalDateTime.now());
    }
}
