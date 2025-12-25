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

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobPostingRepository jobPostingRepository;

    public void apply(Long writerId, ApplicationCreateRequest req) {

        if (applicationRepository.existsByPostingIdAndWriterId(
                req.getPostingId(), writerId)) {
            throw new IllegalStateException("이미 지원한 공고입니다.");
        }

        Application app = Application.builder()
                .postingId(req.getPostingId())
                .writerId(writerId)

                // 기본 정보
                .name(req.getName())
                .birth(parseDate(req.getBirth()))
                .email(req.getEmail())
                .phone(req.getPhone())
                .address(req.getAddress())
                .profileImageUrl(req.getProfileImageUrl())

                // 학력
                .educationSchool(req.getEducationSchool())
                .educationMajor(req.getEducationMajor())
                .educationDegree(req.getEducationDegree())
                .educationStartDate(parseDate(req.getEducationStartDate()))
                .educationEndDate(parseDate(req.getEducationEndDate()))
                .educationGraduated(req.isEducationGraduated())

                // 대외활동
                .activities(req.getActivities())

                // 자기소개
                .introduction(req.getIntroduction())
                .motivation(req.getMotivation())
                .personality(req.getPersonality())
                .futureGoal(req.getFutureGoal())

                // 기타
                .portfolioUrl(req.getPortfolioUrl())

                .status(ApplicationStatus.APPLIED)
                .build();

        applicationRepository.save(app);
    }

    @Transactional(readOnly = true)
    public List<MyApplicationResponse> getMyApplications(Long writerId) {
        return applicationRepository
                .findByWriterIdOrderByCreatedAtDesc(writerId)
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

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value);
    }
}