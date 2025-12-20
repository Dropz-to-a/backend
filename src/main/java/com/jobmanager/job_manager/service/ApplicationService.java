package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.application.ApplicationCreateRequest;
import com.jobmanager.job_manager.dto.application.ApplicationDetailResponse;
import com.jobmanager.job_manager.dto.application.CompanyApplicationResponse;
import com.jobmanager.job_manager.dto.application.MyApplicationResponse;
import com.jobmanager.job_manager.entity.application.Application;
import com.jobmanager.job_manager.entity.application.ApplicationStatus;
import com.jobmanager.job_manager.repository.ApplicationRepository;
import com.jobmanager.job_manager.repository.JobPostingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;

    public void apply(Long applicantId, ApplicationCreateRequest request) {

        if (applicationRepository.existsByPostingIdAndApplicantId(
                request.getPostingId(), applicantId)) {
            throw new IllegalStateException("이미 지원한 공고입니다.");
        }

        Application application = Application.builder()
                .postingId(request.getPostingId())
                .applicantId(applicantId)
                .applicantName(request.getApplicantName())
                .motivation(request.getMotivation())
                .status(ApplicationStatus.APPLIED)
                .build();

        applicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public List<MyApplicationResponse> getMyApplications(Long applicantId) {
        return applicationRepository
                .findByApplicantIdOrderByCreatedAtDesc(applicantId)
                .stream()
                .map(MyApplicationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompanyApplicationResponse> getApplicationsByPosting(
            Long postingId, Long companyAccountId) {

        jobPostingRepository.findByIdAndCompanyId(postingId, companyAccountId)
                .orElseThrow(() -> new IllegalStateException("공고 접근 권한이 없습니다."));

        return applicationRepository.findByPostingId(postingId)
                .stream()
                .map(CompanyApplicationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ApplicationDetailResponse getApplicationDetail(
            Long applicationId, Long companyAccountId) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원서를 찾을 수 없습니다."));

        jobPostingRepository.findByIdAndCompanyId(app.getPostingId(), companyAccountId)
                .orElseThrow(() -> new IllegalStateException("접근 권한이 없습니다."));

        return ApplicationDetailResponse.from(app);
    }

    public void decideResult(
            Long applicationId,
            ApplicationStatus nextStatus,
            Long companyAccountId
    ) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원서를 찾을 수 없습니다."));

        jobPostingRepository.findByIdAndCompanyId(app.getPostingId(), companyAccountId)
                .orElseThrow(() -> new IllegalStateException("접근 권한이 없습니다."));

        // 🔒 종결 상태면 변경 불가
        if (app.getStatus() == ApplicationStatus.HIRED ||
                app.getStatus() == ApplicationStatus.REJECTED) {
            throw new IllegalStateException("이미 결과가 확정된 지원서입니다.");
        }

        // 허용 상태만 변경
        if (nextStatus != ApplicationStatus.HIRED &&
                nextStatus != ApplicationStatus.REJECTED) {
            throw new IllegalArgumentException("합격 또는 불합격만 선택할 수 있습니다.");
        }

        app.setStatus(nextStatus);
    }
}