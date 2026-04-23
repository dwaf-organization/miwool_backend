package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.statistics.respDto.StatisticsDashboardRespDto;
import com.taekwondo.miwool.dto.statistics.respDto.StatisticsDashboardRespDto.*;
import com.taekwondo.miwool.dto.statistics.respDto.StudentManagementSummaryRespDto;
import com.taekwondo.miwool.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StudentRepository studentRepository;
    private final MonthlyBillingRepository monthlyBillingRepository;
    private final StudentPurposeRepository studentPurposeRepository;
    private final StudentCharacterRepository studentCharacterRepository;
    private final StudentEmotionRepository studentEmotionRepository;
    private final StudentSocialRepository studentSocialRepository;
    private final StudentClassResponseRepository studentClassResponseRepository;
    private final StudentImprovementRepository studentImprovementRepository;
    private final StudentStrengthRepository studentStrengthRepository;
    private final StudentCounselRepository studentCounselRepository;
    private final StudentManagementRepository studentManagementRepository;

    @Transactional(readOnly = true)
    public StatisticsDashboardRespDto getStatisticsDashboard(String dojangCode, String month) {
        log.info("통계 대시보드 조회 시작: dojangCode={}, month={}", dojangCode, month);

        // 1. 월별 재원현황 (12개월)
        List<MonthlyStatusDto> monthlyStatus = getMonthlyStatus(dojangCode, month);

        // 2. 패키지분포
        List<PackageDistributionDto> packageDistribution = getPackageDistribution(dojangCode, month);

        // 3. 등록목적
        List<CodeCountDto> enrollPurpose = getCodeCountList(
            studentPurposeRepository.getPurposeStats(dojangCode));

        // 4. 성향/특성
        CharacteristicsDto characteristics = getCharacteristics(dojangCode);

        // 5. 상담통계
        List<CounselStatsDto> counselStats = getCounselStats(dojangCode, month);

        // 6. 교육통계
        List<EducationStatsDto> educationStats = getEducationStats(dojangCode, month);

        log.info("통계 대시보드 조회 완료");

        return StatisticsDashboardRespDto.builder()
                .month(month)
                .monthlyStatus(monthlyStatus)
                .packageDistribution(packageDistribution)
                .enrollPurpose(enrollPurpose)
                .characteristics(characteristics)
                .counselStats(counselStats)
                .educationStats(educationStats)
                .build();
    }

    // 월별 재원현황 (12개월)
    private List<MonthlyStatusDto> getMonthlyStatus(String dojangCode, String month) {
        List<MonthlyStatusDto> result = new ArrayList<>();
        
        YearMonth currentMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyyMM"));
        
        // 현재월부터 11개월 전까지 (총 12개월)
        for (int i = 11; i >= 0; i--) {
            YearMonth targetMonth = currentMonth.minusMonths(i);
            String targetMonthStr = targetMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
            
            int enrolled = studentRepository.countMonthlyEnrolledByMonth(dojangCode, targetMonthStr);
            int trial = studentRepository.countMonthlyTrialByMonth(dojangCode, targetMonthStr);
            int withdrawn = studentRepository.countMonthlyWithdrawnByMonth(dojangCode, targetMonthStr);
            
            result.add(MonthlyStatusDto.builder()
                    .month(targetMonthStr)
                    .enrolled(enrolled)
                    .trial(trial)
                    .withdrawn(withdrawn)
                    .build());
        }
        
        return result;
    }

    // 패키지분포
    private List<PackageDistributionDto> getPackageDistribution(String dojangCode, String month) {
        // billing_month 형식: "2026-04"
        YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyyMM"));
        String billingMonth = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        List<Object[]> rawData = monthlyBillingRepository.getPackageDistribution(dojangCode, billingMonth);
        List<PackageDistributionDto> result = new ArrayList<>();
        
        for (Object[] row : rawData) {
            result.add(PackageDistributionDto.builder()
                    .packageCode((String) row[0])
                    .packageName((String) row[1])
                    .studentCount(((Number) row[2]).intValue())
                    .build());
        }
        
        return result;
    }

    // 공통 코드 카운트 변환
    private List<CodeCountDto> getCodeCountList(List<Object[]> rawData) {
        List<CodeCountDto> result = new ArrayList<>();
        
        for (Object[] row : rawData) {
            result.add(CodeCountDto.builder()
                    .code((String) row[0])
                    .name((String) row[1])
                    .count(((Number) row[2]).intValue())
                    .build());
        }
        
        return result;
    }

    // 성향/특성
    private CharacteristicsDto getCharacteristics(String dojangCode) {
        return CharacteristicsDto.builder()
                .baseCharacter(getCodeCountList(studentCharacterRepository.getCharacterStats(dojangCode)))
                .emotion(getCodeCountList(studentEmotionRepository.getEmotionStats(dojangCode)))
                .social(getCodeCountList(studentSocialRepository.getSocialStats(dojangCode)))
                .classResponse(getCodeCountList(studentClassResponseRepository.getClassResponseStats(dojangCode)))
                .improvement(getCodeCountList(studentImprovementRepository.getImprovementStats(dojangCode)))
                .strength(getCodeCountList(studentStrengthRepository.getStrengthStats(dojangCode)))
                .build();
    }

    // 상담통계
    private List<CounselStatsDto> getCounselStats(String dojangCode, String month) {
        List<Object[]> rawData = studentCounselRepository.getCounselStats(dojangCode, month);
        List<CounselStatsDto> result = new ArrayList<>();
        
        for (Object[] row : rawData) {
            result.add(CounselStatsDto.builder()
                    .counselType((String) row[0])
                    .count(((Number) row[1]).intValue())
                    .build());
        }
        
        return result;
    }

    // 교육통계
    private List<EducationStatsDto> getEducationStats(String dojangCode, String month) {
        List<Object[]> rawData = studentManagementRepository.getEducationStats(dojangCode, month);
        
        // 전체 재원생 수
        int totalCount = studentRepository.countCurrentTotal(dojangCode);
        
        List<EducationStatsDto> result = new ArrayList<>();
        
        for (Object[] row : rawData) {
            String managementType = (String) row[0];
            int completedCount = ((Number) row[1]).intValue();
            
            double completionRate = totalCount > 0 
                ? Math.round((double) completedCount / totalCount * 100 * 100.0) / 100.0
                : 0.0;
            
            result.add(EducationStatsDto.builder()
                    .managementType(managementType)
                    .completedCount(completedCount)
                    .totalCount(totalCount)
                    .completionRate(completionRate)
                    .build());
        }
        
        return result;
    }

    @Transactional(readOnly = true)
    public StudentManagementSummaryRespDto getStudentManagementSummary(String dojangCode, String month) {
        log.info("제자관리결산 조회 시작: dojangCode={}, month={}", dojangCode, month);

        YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyyMM"));
        YearMonth previousYearMonth = yearMonth.minusMonths(1);
        String previousMonth = previousYearMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
        
        String billingMonth = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String previousBillingMonth = previousYearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // 1. 납부금
        StudentManagementSummaryRespDto.PaymentDto payment = getPaymentSummary(dojangCode, billingMonth);

        // 2. 재원상태별 집계
        StudentManagementSummaryRespDto.StudentStatusDto studentStatus = getStudentStatusSummary(dojangCode, month, previousMonth);

        // 3. 교육비결산
        StudentManagementSummaryRespDto.RevenueSummaryDto revenueSummary = getRevenueSummary(dojangCode, billingMonth, previousBillingMonth);

        // 4. 성별매출
        List<StudentManagementSummaryRespDto.GenderRevenueDto> genderRevenue = getGenderRevenueSummary(dojangCode, billingMonth, previousBillingMonth);

        // 5. 연령별매출
        List<StudentManagementSummaryRespDto.AgeRevenueDto> ageRevenue = getAgeRevenueSummary(dojangCode, billingMonth, previousBillingMonth);

        // 6. 교육지도
        StudentManagementSummaryRespDto.EducationGuideDto educationGuide = getEducationGuideSummary(dojangCode, month);

        log.info("제자관리결산 조회 완료");

        return StudentManagementSummaryRespDto.builder()
                .month(month)
                .payment(payment)
                .studentStatus(studentStatus)
                .revenueSummary(revenueSummary)
                .genderRevenue(genderRevenue)
                .ageRevenue(ageRevenue)
                .educationGuide(educationGuide)
                .build();
    }

    // 납부금
    private StudentManagementSummaryRespDto.PaymentDto getPaymentSummary(String dojangCode, String billingMonth) {
        int paid = monthlyBillingRepository.getPaidAmount(dojangCode, billingMonth);
        int unpaid = monthlyBillingRepository.getUnpaidAmount(dojangCode, billingMonth);
        
        return StudentManagementSummaryRespDto.PaymentDto.builder()
                .paid(paid)
                .unpaid(unpaid)
                .build();
    }

    // 재원상태별 집계
    private StudentManagementSummaryRespDto.StudentStatusDto getStudentStatusSummary(String dojangCode, String month, String previousMonth) {
        // 신규입관
        int currentEnrollment = studentRepository.countMonthlyEnrolledByMonth(dojangCode, month);
        int previousEnrollment = studentRepository.countMonthlyEnrolledByMonth(dojangCode, previousMonth);
        
        // 재원
        int currentTotal = studentRepository.countCurrentTotal(dojangCode);
        // 전월 재원생 수 계산 (현재 - 이번달입관 + 이번달퇴관)
        int currentMonthEnrolled = studentRepository.countMonthlyEnrollment(dojangCode, month);
        int currentMonthWithdrawn = studentRepository.countMonthlyWithdrawal(dojangCode, month);
        int previousTotal = currentTotal - currentMonthEnrolled + currentMonthWithdrawn;
        
        // 퇴관
        int currentWithdrawal = studentRepository.countMonthlyWithdrawnByMonth(dojangCode, month);
        int previousWithdrawal = studentRepository.countMonthlyWithdrawnByMonth(dojangCode, previousMonth);
        
        // 체험
        int currentTrial = studentRepository.countMonthlyTrialByMonth(dojangCode, month);
        int previousTrial = studentRepository.countMonthlyTrialByMonth(dojangCode, previousMonth);
        
        return StudentManagementSummaryRespDto.StudentStatusDto.builder()
                .newEnrollment(StudentManagementSummaryRespDto.StatusCountDto.builder()
                        .current(currentEnrollment)
                        .previous(previousEnrollment)
                        .change(currentEnrollment - previousEnrollment)
                        .build())
                .enrolled(StudentManagementSummaryRespDto.StatusCountDto.builder()
                        .current(currentTotal)
                        .previous(previousTotal)
                        .change(currentTotal - previousTotal)
                        .build())
                .withdrawn(StudentManagementSummaryRespDto.StatusCountDto.builder()
                        .current(currentWithdrawal)
                        .previous(previousWithdrawal)
                        .change(currentWithdrawal - previousWithdrawal)
                        .build())
                .trial(StudentManagementSummaryRespDto.StatusCountDto.builder()
                        .current(currentTrial)
                        .previous(previousTrial)
                        .change(currentTrial - previousTrial)
                        .build())
                .build();
    }

    // 교육비결산
    private StudentManagementSummaryRespDto.RevenueSummaryDto getRevenueSummary(String dojangCode, String billingMonth, String previousBillingMonth) {
        // 총 매출
        int totalRevenue = monthlyBillingRepository.getPaidAmount(dojangCode, billingMonth);
        
        // 패키지별 매출
        List<Object[]> packageData = monthlyBillingRepository.getPackageRevenue(dojangCode, billingMonth);
        List<Object[]> previousPackageData = monthlyBillingRepository.getPackageRevenue(dojangCode, previousBillingMonth);
        
        // 전월 매출 Map
        Map<String, Integer> previousRevenueMap = new HashMap<>();
        for (Object[] row : previousPackageData) {
            String packageCode = (String) row[0];
            int revenue = ((Number) row[3]).intValue();
            previousRevenueMap.put(packageCode, revenue);
        }
        
        List<StudentManagementSummaryRespDto.PackageRevenueDto> packageRevenue = new ArrayList<>();
        for (Object[] row : packageData) {
            String packageCode = (String) row[0];
            String packageName = (String) row[1];
            int studentCount = ((Number) row[2]).intValue();
            int revenue = ((Number) row[3]).intValue();
            int previousRevenue = previousRevenueMap.getOrDefault(packageCode, 0);
            
            double changeRate = calculateChangeRate(revenue, previousRevenue);
            
            packageRevenue.add(StudentManagementSummaryRespDto.PackageRevenueDto.builder()
                    .packageCode(packageCode)
                    .packageName(packageName)
                    .studentCount(studentCount)
                    .revenue(revenue)
                    .previousRevenue(previousRevenue)
                    .changeRate(changeRate)
                    .build());
        }
        
        return StudentManagementSummaryRespDto.RevenueSummaryDto.builder()
                .totalRevenue(totalRevenue)
                .packageRevenue(packageRevenue)
                .build();
    }

    // 성별매출
    private List<StudentManagementSummaryRespDto.GenderRevenueDto> getGenderRevenueSummary(String dojangCode, String billingMonth, String previousBillingMonth) {
        // 성별 인원수
        List<Object[]> countData = studentRepository.countByGender(dojangCode);
        Map<Integer, Integer> countMap = new HashMap<>();
        for (Object[] row : countData) {
            countMap.put((Integer) row[0], ((Number) row[1]).intValue());
        }
        
        // 성별 매출
        List<Object[]> revenueData = monthlyBillingRepository.getGenderRevenue(dojangCode, billingMonth);
        Map<Integer, Integer> revenueMap = new HashMap<>();
        for (Object[] row : revenueData) {
            revenueMap.put((Integer) row[0], ((Number) row[1]).intValue());
        }
        
        // 전월 매출
        List<Object[]> previousRevenueData = monthlyBillingRepository.getGenderRevenue(dojangCode, previousBillingMonth);
        Map<Integer, Integer> previousRevenueMap = new HashMap<>();
        for (Object[] row : previousRevenueData) {
            previousRevenueMap.put((Integer) row[0], ((Number) row[1]).intValue());
        }
        
        // 성별명 매핑
        Map<Integer, String> genderNameMap = new HashMap<>();
        genderNameMap.put(1, "남성");
        genderNameMap.put(2, "여성");
        genderNameMap.put(3, "기타");
        
        List<StudentManagementSummaryRespDto.GenderRevenueDto> result = new ArrayList<>();
        for (Integer genderCode : new Integer[]{1, 2, 3}) {
            int count = countMap.getOrDefault(genderCode, 0);
            int revenue = revenueMap.getOrDefault(genderCode, 0);
            int previousRevenue = previousRevenueMap.getOrDefault(genderCode, 0);
            double changeRate = calculateChangeRate(revenue, previousRevenue);
            
            result.add(StudentManagementSummaryRespDto.GenderRevenueDto.builder()
                    .gender(genderNameMap.get(genderCode))
                    .genderCode(genderCode)
                    .studentCount(count)
                    .revenue(revenue)
                    .previousRevenue(previousRevenue)
                    .changeRate(changeRate)
                    .build());
        }
        
        return result;
    }

    // 연령별매출
    private List<StudentManagementSummaryRespDto.AgeRevenueDto> getAgeRevenueSummary(String dojangCode, String billingMonth, String previousBillingMonth) {
        // 연령대별 인원수
        List<Object[]> countData = studentRepository.countByAgeGroup(dojangCode);
        Map<String, Integer> countMap = new HashMap<>();
        for (Object[] row : countData) {
            countMap.put((String) row[0], ((Number) row[1]).intValue());
        }
        
        // 연령대별 매출
        List<Object[]> revenueData = monthlyBillingRepository.getAgeRevenue(dojangCode, billingMonth);
        Map<String, Integer> revenueMap = new HashMap<>();
        for (Object[] row : revenueData) {
            revenueMap.put((String) row[0], ((Number) row[1]).intValue());
        }
        
        // 전월 매출
        List<Object[]> previousRevenueData = monthlyBillingRepository.getAgeRevenue(dojangCode, previousBillingMonth);
        Map<String, Integer> previousRevenueMap = new HashMap<>();
        for (Object[] row : previousRevenueData) {
            previousRevenueMap.put((String) row[0], ((Number) row[1]).intValue());
        }
        
        List<StudentManagementSummaryRespDto.AgeRevenueDto> result = new ArrayList<>();
        for (String ageGroup : new String[]{"유아", "초등부", "중등부", "고등부", "성인부"}) {
            int count = countMap.getOrDefault(ageGroup, 0);
            int revenue = revenueMap.getOrDefault(ageGroup, 0);
            int previousRevenue = previousRevenueMap.getOrDefault(ageGroup, 0);
            double changeRate = calculateChangeRate(revenue, previousRevenue);
            
            result.add(StudentManagementSummaryRespDto.AgeRevenueDto.builder()
                    .ageGroup(ageGroup)
                    .studentCount(count)
                    .revenue(revenue)
                    .previousRevenue(previousRevenue)
                    .changeRate(changeRate)
                    .build());
        }
        
        return result;
    }

    // 교육지도
    private StudentManagementSummaryRespDto.EducationGuideDto getEducationGuideSummary(String dojangCode, String month) {
        // 유형별 실시 인원
        List<Object[]> statsData = studentManagementRepository.getEducationStats(dojangCode, month);
        List<StudentManagementSummaryRespDto.GuideStatsDto> statistics = new ArrayList<>();
        
        for (Object[] row : statsData) {
            String guideType = (String) row[0];
            int completedCount = ((Number) row[1]).intValue();
            
            // 해당 유형 미실시 명단 조회
            List<Object[]> notCompletedData = studentManagementRepository.getNotCompletedByType(dojangCode, month, guideType);
            List<StudentManagementSummaryRespDto.NotCompletedDto> notCompleted = new ArrayList<>();
            
            for (Object[] ncRow : notCompletedData) {
                notCompleted.add(StudentManagementSummaryRespDto.NotCompletedDto.builder()
                        .studentName((String) ncRow[0])
                        .genderCode((Integer) ncRow[1])
                        .build());
            }
            
            statistics.add(StudentManagementSummaryRespDto.GuideStatsDto.builder()
                    .guideType(guideType)
                    .completedCount(completedCount)
                    .notCompleted(notCompleted)
                    .build());
        }
        
        return StudentManagementSummaryRespDto.EducationGuideDto.builder()
                .statistics(statistics)
                .build();
    }

    // 증감율 계산 (재사용)
    private Double calculateChangeRate(int current, int previous) {
        if (previous == 0) {
            return current == 0 ? 0.0 : 100.0;
        }
        return Math.round(((double) (current - previous) / previous) * 100 * 100.0) / 100.0;
    }
    
}