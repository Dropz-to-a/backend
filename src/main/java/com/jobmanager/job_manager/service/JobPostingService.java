package com.jobmanager.job_manager.service;

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
     * 내 공고 관리 목록 조회
     * - 모집중 + 마감 공고
     * - 지원자 수 포함
     */
    public List<JobPostingManageResponse> getMyPostings(Long companyId) {

        List<JobPosting> postings =
                jobPostingRepository.findByCompanyIdAndStatusIn(
                        companyId,
                        List.of(JobPostingStatus.OPEN, JobPostingStatus.CLOSED)
                );

        return postings.stream()
                .map(posting -> JobPostingManageResponse.from(
                        posting,
                        applicationRepository.countByPostingId(posting.getId())
                ))
                .toList();
    }

    /**
     * 공고 기록 조회
     * - 마감된 공고만
     * - 최신순
     */
    public List<JobPostingHistoryResponse> getPostingHistory(Long companyId) {

        List<JobPosting> postings =
                jobPostingRepository.findByCompanyIdAndStatusOrderByClosedAtDesc(
                        companyId,
                        JobPostingStatus.CLOSED
                );

        return postings.stream()
                .map(posting -> JobPostingHistoryResponse.from(
                        posting,
                        applicationRepository.countByPostingId(posting.getId())
                ))
                .toList();
    }

    /**
     * 공고 삭제 (실제 삭제 ❌)
     * - status 를 CLOSED 로 변경
     * - 지원자가 1명이라도 있으면 불가
     */
    @Transactional
    public void closePosting(Long postingId, Long companyId) {

        JobPosting posting = jobPostingRepository
                .findByIdAndCompanyId(postingId, companyId)
                .orElseThrow(() ->
                        new IllegalArgumentException("공고를 찾을 수 없습니다.")
                );

        int applicantCount =
                applicationRepository.countByPostingId(postingId);

        if (applicantCount > 0) {
            throw new IllegalStateException(
                    "지원자가 존재하는 공고는 삭제할 수 없습니다."
            );
        }

        posting.setStatus(JobPostingStatus.CLOSED);
        posting.setClosedAt(LocalDateTime.now());
    }
}
