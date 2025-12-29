package com.jobmanager.job_manager.service;

import com.jobmanager.job_manager.dto.application.ApplicationCreateRequest;
import com.jobmanager.job_manager.dto.application.ApplicationDetailResponse;
import com.jobmanager.job_manager.dto.application.CompanyApplicationResponse;
import com.jobmanager.job_manager.dto.application.MyApplicationResponse;
import com.jobmanager.job_manager.entity.UserFamily;
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
    private final UserFamilyService userFamilyService;

    /**
     * 채용 공고 지원
     */
    public void apply(Long writerId, ApplicationCreateRequest req) {

        // 중복 지원 방지
        if (applicationRepository.existsByPostingIdAndWriterId(
                req.getPostingId(), writerId)) {
            throw new IllegalStateException("이미 지원한 공고입니다.");
        }

        Application.ApplicationBuilder builder = Application.builder()
                .postingId(req.getPostingId())
                .writerId(writerId)

                // =====================
                // 기본 정보
                // =====================
                .name(req.getName())
                .birth(parseDate(req.getBirth()))
                .email(req.getEmail())
                .phone(req.getPhone())
                .address(req.getAddress())
                .profileImageUrl(req.getProfileImageUrl())

                // =====================
                // 학력
                // =====================
                .educationSchool(req.getEducationSchool())
                .educationMajor(req.getEducationMajor())
                .educationDegree(req.getEducationDegree())
                .educationStartDate(parseDate(req.getEducationStartDate()))
                .educationEndDate(parseDate(req.getEducationEndDate()))
                .educationGraduated(req.isEducationGraduated())

                // =====================
                // 신체
                // =====================
                .height(req.getHeight())
                .weight(req.getWeight())
                .blood(req.getBlood())

                // =====================
                // 병역
                // =====================
                .militaryStatus(req.getMilitaryStatus())
                .militaryBranch(req.getMilitaryBranch())
                .militaryType(req.getMilitaryType())
                .militaryRank(req.getMilitaryRank())
                .militaryPeriod(req.getMilitaryPeriod())
                .militaryExemptReason(req.getMilitaryExemptReason())

                // =====================
                // 수상 내역
                // =====================
                .awardName1(req.getAwardName1())
                .awardDate1(parseDate(req.getAwardDate1()))
                .awardIssuer1(req.getAwardIssuer1())

                .awardName2(req.getAwardName2())
                .awardDate2(parseDate(req.getAwardDate2()))
                .awardIssuer2(req.getAwardIssuer2())

                .awardName3(req.getAwardName3())
                .awardDate3(parseDate(req.getAwardDate3()))
                .awardIssuer3(req.getAwardIssuer3())

                // =====================
                // 외국어 활용 능력
                // =====================
                .foreignLangAbility1(req.getForeignLangAbility1())
                .foreignLangTest1(req.getForeignLangTest1())
                .foreignLangScore1(req.getForeignLangScore1())

                .foreignLangAbility2(req.getForeignLangAbility2())
                .foreignLangTest2(req.getForeignLangTest2())
                .foreignLangScore2(req.getForeignLangScore2())

                // =====================
                // 자격증
                // =====================
                .licenseType1(req.getLicenseType1())
                .licenseLevel1(req.getLicenseLevel1())
                .licenseDate1(parseDate(req.getLicenseDate1()))
                .licenseIssuer1(req.getLicenseIssuer1())

                .licenseType2(req.getLicenseType2())
                .licenseLevel2(req.getLicenseLevel2())
                .licenseDate2(parseDate(req.getLicenseDate2()))
                .licenseIssuer2(req.getLicenseIssuer2())

                .licenseType3(req.getLicenseType3())
                .licenseLevel3(req.getLicenseLevel3())
                .licenseDate3(parseDate(req.getLicenseDate3()))
                .licenseIssuer3(req.getLicenseIssuer3())

                // =====================
                // 대외활동
                // =====================
                .activities(req.getActivities())

                // =====================
                // 자기소개
                // =====================
                .introduction(req.getIntroduction())
                .motivation(req.getMotivation())
                .personality(req.getPersonality())
                .futureGoal(req.getFutureGoal())

                // =====================
                // 취미 / 특기
                // =====================
                .hobby(req.getHobby())
                .specialty(req.getSpecialty())

                // =====================
                // 기타
                // =====================
                .portfolioUrl(req.getPortfolioUrl())

                .status(ApplicationStatus.APPLIED);

        // =====================
        // 가족 자동 주입 (최대 4명)
        // =====================
        fillFamilyIfEmpty(builder, req, writerId);

        applicationRepository.save(builder.build());
    }

    // =====================
    // USER - 내 지원서 목록
    // =====================
    @Transactional(readOnly = true)
    public List<MyApplicationResponse> getMyApplications(Long writerId) {
        return applicationRepository
                .findByWriterIdOrderByCreatedAtDesc(writerId)
                .stream()
                .map(MyApplicationResponse::from)
                .toList();
    }

    // =====================
    // COMPANY - 공고별 지원서 목록
    // =====================
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

    // =====================
    // COMPANY - 지원서 상세 조회
    // =====================
    @Transactional(readOnly = true)
    public ApplicationDetailResponse getApplicationDetail(
            Long applicationId, Long companyAccountId) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원서를 찾을 수 없습니다."));

        jobPostingRepository.findByIdAndCompanyId(app.getPostingId(), companyAccountId)
                .orElseThrow(() -> new IllegalStateException("접근 권한이 없습니다."));

        return ApplicationDetailResponse.from(app);
    }

    // =====================
    // COMPANY - 합격 / 불합격 결정
    // =====================
    public void decideResult(
            Long applicationId,
            ApplicationStatus nextStatus,
            Long companyAccountId
    ) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("지원서를 찾을 수 없습니다."));

        jobPostingRepository.findByIdAndCompanyId(app.getPostingId(), companyAccountId)
                .orElseThrow(() -> new IllegalStateException("접근 권한이 없습니다."));

        if (app.getStatus() == ApplicationStatus.HIRED ||
                app.getStatus() == ApplicationStatus.REJECTED) {
            throw new IllegalStateException("이미 결과가 확정된 지원서입니다.");
        }

        if (nextStatus != ApplicationStatus.HIRED &&
                nextStatus != ApplicationStatus.REJECTED) {
            throw new IllegalArgumentException("합격 또는 불합격만 선택할 수 있습니다.");
        }

        app.setStatus(nextStatus);
    }

    private void fillFamilyIfEmpty(
            Application.ApplicationBuilder builder,
            ApplicationCreateRequest req,
            Long writerId
    ) {
        List<UserFamily> families = userFamilyService.getFamilies(writerId);

        UserFamily f1 = families.size() > 0 ? families.get(0) : null;
        UserFamily f2 = families.size() > 1 ? families.get(1) : null;
        UserFamily f3 = families.size() > 2 ? families.get(2) : null;
        UserFamily f4 = families.size() > 3 ? families.get(3) : null;

        // 1번
        builder.familyRelation1(
                valueOr(req.getFamilyRelation1(), f1 != null ? f1.getRole() : null)
        );
        builder.familyName1(
                valueOr(req.getFamilyName1(), f1 != null ? f1.getName() : null)
        );
        builder.familyAge1(
                valueOr(req.getFamilyAge1(), f1 != null && f1.getAge() != null ? String.valueOf(f1.getAge()) : null)
        );
        builder.familyJob1(
                valueOr(req.getFamilyJob1(), f1 != null ? f1.getJob() : null)
        );
    }

    // =====================
    // 공통 날짜 파서
    // =====================
    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value);
    }

    private String valueOr(String reqValue, String defaultValue) {
        return (reqValue != null && !reqValue.isBlank())
                ? reqValue
                : defaultValue;
    }
}