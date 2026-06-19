package com.taekwondo.miwool.service.admin;

import com.taekwondo.miwool.dto.admin.dashboard.respDto.*;
import com.taekwondo.miwool.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final SignupAlarmRepository signupAlarmRepository;
    private final DojangRepository dojangRepository;
    private final StudentRepository studentRepository;
    private final MonthlyBillingRepository monthlyBillingRepository;
    private final StudentManagementRepository studentManagementRepository;

    /**
     * 관리자 대시보드 데이터 조회
     */
    @Transactional(readOnly = true)
    public DashboardRespDto getDashboard(String month) {
        log.info("관리자 대시보드 조회: month={}", month);
        
        // 미래 날짜 체크
        YearMonth requestMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        YearMonth currentMonth = YearMonth.now();
        
        if (requestMonth.isAfter(currentMonth)) {
            throw new IllegalArgumentException("미래 날짜는 조회할 수 없습니다.");
        }
        
        // 1. KPI 카드 데이터
        KpiDto kpi = getKpiData(month);
        
        // 2. 교육관리 실행률 TOP 10
        List<EducationRankingDto> educationRanking = getEducationRanking(month);
        
        // 3. 제자수 TOP 5
        List<StudentRankingDto> studentRanking = getStudentRanking();
        
        // 4. 재원현황 TOP 3
        List<EnrollmentStatusDto> enrollmentStatus = getEnrollmentStatus();
        
        // 5. 매출비교 TOP 5
        List<RevenueComparisonDto> revenueComparison = getRevenueComparison(month);
        
        // 6. 상세 비교표 TOP 5
        List<DetailedComparisonDto> detailedComparison = getDetailedComparison(month);
        
        return DashboardRespDto.builder()
                .kpi(kpi)
                .educationRanking(educationRanking)
                .studentRanking(studentRanking)
                .enrollmentStatus(enrollmentStatus)
                .revenueComparison(revenueComparison)
                .detailedComparison(detailedComparison)
                .build();
    }

    /**
     * KPI 카드 데이터 조회
     */
    private KpiDto getKpiData(String month) {
        // 신규가입대기건수
        Long pendingSignups = signupAlarmRepository.countByApprovalStatus(0);
        
        // 전체도장수
        Long totalDojangs = dojangRepository.countByIsDeleted(0);
        
        // 전체제자수 (재원 + 체험)
        Long totalStudents = studentRepository.countByStudentStatusInAndIsDeleted(
                List.of("재원", "체험"), 0);
        
        // 이번달 총매출
        Long monthlyRevenue = monthlyBillingRepository
                .sumPaymentAmountByBillingMonthAndBillingStatus(month, "납부완료");
        
        if (monthlyRevenue == null) {
            monthlyRevenue = 0L;
        }
        
        return KpiDto.builder()
                .pendingSignups(pendingSignups)
                .totalDojangs(totalDojangs)
                .totalStudents(totalStudents)
                .monthlyRevenue(monthlyRevenue)
                .build();
    }

    /**
     * 교육관리 실행률 TOP 10 조회
     */
    private List<EducationRankingDto> getEducationRanking(String month) {
        // 연도와 월 추출
        YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        int year = yearMonth.getYear();
        int monthValue = yearMonth.getMonthValue();
        
        // 각 도장별 재원 제자수 조회
        List<Object[]> enrolledCounts = studentRepository.countEnrolledByDojang();
        
        // 각 도장별 교육 실행 건수 조회 (타입별)
        List<Object[]> executionCounts = studentManagementRepository
                .countExecutionsByDojangAndType(year, monthValue);
        
        // 도장별로 그룹화
        Map<String, Long> enrolledMap = new HashMap<>();
        for (Object[] row : enrolledCounts) {
            String dojangCode = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            enrolledMap.put(dojangCode, count);
        }
        
        Map<String, Map<String, Long>> executionMap = new HashMap<>();
        for (Object[] row : executionCounts) {
            String dojangCode = (String) row[0];
            String dojangName = (String) row[1];
            String managementType = (String) row[2];
            Long count = ((Number) row[3]).longValue();
            
            executionMap.putIfAbsent(dojangCode, new HashMap<>());
            executionMap.get(dojangCode).put("dojangName", dojangName.hashCode() * 1L); // 임시
            executionMap.get(dojangCode).put(managementType, count);
        }
        
        // 도장별로 실행률 계산
        List<EducationRankingDto> rankings = new ArrayList<>();
        
        for (String dojangCode : executionMap.keySet()) {
            Long enrolledCount = enrolledMap.getOrDefault(dojangCode, 0L);
            if (enrolledCount == 0) continue;
            
            Map<String, Long> executions = executionMap.get(dojangCode);
            
            // 각 교육별 실행률 계산
            double phoneRate = calculateRate(executions.getOrDefault("전화", 0L), enrolledCount);
            double smsRate = calculateRate(executions.getOrDefault("문자", 0L), enrolledCount);
            double letterRate = calculateRate(executions.getOrDefault("손편지", 0L), enrolledCount);
            double snackRate = calculateRate(executions.getOrDefault("간식", 0L), enrolledCount);
            double certificateRate = calculateRate(executions.getOrDefault("상장", 0L), enrolledCount);
            double videoRate = calculateRate(executions.getOrDefault("영상", 0L), enrolledCount);
            double observationRate = calculateRate(executions.getOrDefault("관찰지", 0L), enrolledCount);
            
            // 평균 점수 계산
            double averageScore = (phoneRate + smsRate + letterRate + snackRate + 
                    certificateRate + videoRate + observationRate) / 7.0;
            
            // 도장명 조회
            String dojangName = dojangRepository.findById(dojangCode)
                    .map(d -> d.getDojangName())
                    .orElse("알 수 없음");
            
            EducationRankingDto.ExecutionRatesDto rates = EducationRankingDto.ExecutionRatesDto.builder()
                    .phone(phoneRate)
                    .sms(smsRate)
                    .letter(letterRate)
                    .snack(snackRate)
                    .certificate(certificateRate)
                    .video(videoRate)
                    .observation(observationRate)
                    .build();
            
            EducationRankingDto ranking = EducationRankingDto.builder()
                    .dojangName(dojangName)
                    .totalStudents(enrolledCount)
                    .executionRates(rates)
                    .averageScore(Math.round(averageScore * 10.0) / 10.0)
                    .build();
            
            rankings.add(ranking);
        }
        
        // 종합점수 높은 순으로 정렬 후 TOP 10
        return rankings.stream()
                .sorted((a, b) -> Double.compare(b.getAverageScore(), a.getAverageScore()))
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * 실행률 계산 헬퍼 메서드
     */
    private double calculateRate(Long executed, Long total) {
        if (total == 0) return 0.0;
        return Math.round((executed.doubleValue() / total.doubleValue() * 100.0) * 10.0) / 10.0;
    }

    /**
     * 제자수 TOP 5 조회
     */
    private List<StudentRankingDto> getStudentRanking() {
        List<Object[]> results = studentRepository.findTop5ByStudentCount();
        
        return results.stream()
                .map(row -> StudentRankingDto.builder()
                        .dojangName((String) row[0])
                        .studentCount(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 재원현황 TOP 3 조회
     */
    private List<EnrollmentStatusDto> getEnrollmentStatus() {
        List<Object[]> results = studentRepository.findTop3ByEnrollmentStatus();
        
        return results.stream()
                .map(row -> EnrollmentStatusDto.builder()
                        .dojangName((String) row[0])
                        .enrolled(((Number) row[1]).longValue())
                        .withdrawn(((Number) row[2]).longValue())
                        .trial(((Number) row[3]).longValue())
                        .suspended(((Number) row[4]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 매출비교 TOP 5 조회
     */
    private List<RevenueComparisonDto> getRevenueComparison(String month) {
        List<Object[]> results = monthlyBillingRepository.findTop5RevenueByMonth(month);
        
        return results.stream()
                .map(row -> RevenueComparisonDto.builder()
                        .dojangName((String) row[0])
                        .revenue(((Number) row[1]).longValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 상세 비교표 TOP 5 조회
     */
    private List<DetailedComparisonDto> getDetailedComparison(String month) {
        // 전월 계산
        YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        YearMonth prevYearMonth = yearMonth.minusMonths(1);
        String prevMonth = prevYearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // 이번달 매출 TOP 5 도장 조회
        List<Object[]> currentRevenues = monthlyBillingRepository.findTop5RevenueByMonth(month);
        
        List<DetailedComparisonDto> comparisons = new ArrayList<>();
        
        for (Object[] row : currentRevenues) {
            String dojangName = (String) row[0];
            String dojangCode = (String) row[2]; // dojangCode 추가 필요
            Long currentRevenue = ((Number) row[1]).longValue();
            
            // 도장 정보 조회
            String masterName = dojangRepository.findById(dojangCode)
                    .map(d -> d.getMasterName())
                    .orElse("알 수 없음");
            
            // 제자수 조회 (재원 + 체험)
            Long studentCount = studentRepository.countByDojangCodeAndStudentStatusInAndIsDeleted(
                    dojangCode, List.of("재원", "체험"), 0);
            
            // 재원율 계산
            Long enrolled = studentRepository.countByDojangCodeAndStudentStatusAndIsDeleted(
                    dojangCode, "재원", 0);
            Long withdrawn = studentRepository.countByDojangCodeAndStudentStatusAndIsDeleted(
                    dojangCode, "퇴관", 0);
            Long trial = studentRepository.countByDojangCodeAndStudentStatusAndIsDeleted(
                    dojangCode, "체험", 0);
            
            Long total = enrolled + withdrawn + trial;
            Double enrollmentRate = total > 0 
                    ? Math.round((enrolled.doubleValue() / total.doubleValue() * 100.0) * 10.0) / 10.0 
                    : 0.0;
            
            // 전월 매출 조회
            Long prevRevenue = monthlyBillingRepository
                    .sumPaymentAmountByDojangCodeAndBillingMonthAndBillingStatus(
                            dojangCode, prevMonth, "납부완료");
            
            if (prevRevenue == null) {
                prevRevenue = 0L;
            }
            
            // 전월대비 증감율 계산
            Double growth;
            if (prevRevenue == 0) {
                growth = currentRevenue > 0 ? 100.0 : 0.0;
            } else {
            	growth = ((double)(currentRevenue - prevRevenue) / prevRevenue) * 100.0;
                growth = Math.round(growth * 10.0) / 10.0;
            }
            
            DetailedComparisonDto comparison = DetailedComparisonDto.builder()
                    .dojangName(dojangName)
                    .masterName(masterName)
                    .studentCount(studentCount)
                    .enrollmentRate(enrollmentRate)
                    .revenue(currentRevenue)
                    .monthOverMonthGrowth(growth)
                    .build();
            
            comparisons.add(comparison);
        }
        
        return comparisons;
    }
}