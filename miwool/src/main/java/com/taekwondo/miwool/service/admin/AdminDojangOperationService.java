package com.taekwondo.miwool.service.admin;

import com.taekwondo.miwool.dto.admin.dojang.respDto.*;
import com.taekwondo.miwool.repository.*;
import com.taekwondo.miwool.util.AgeUtil;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminDojangOperationService {

    private final StudentRepository studentRepository;
    private final StudentStatusRepository studentStatusRepository;
    private final MonthlyBillingRepository monthlyBillingRepository;
    private final TrainingMstRepository trainingMstRepository;

    /**
     * 도장 운영현황 조회
     */
    @Transactional(readOnly = true)
    public DojangOperationRespDto getDojangOperation(String dojangCode, String month) {
        log.info("도장 운영현황 조회: dojangCode={}, month={}", dojangCode, month);
        
        YearMonth targetMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // 1. KPI 조회
        OperationKpiDto kpi = getKpi(dojangCode, targetMonth);
        
        // 2. 월별 제자 추이 (6개월)
        List<MonthlyStudentTrendDto> studentTrend = getStudentTrend(dojangCode, targetMonth);
        
        // 3. 월별 매출 추이 (6개월)
        List<MonthlyRevenueTrendDto> revenueTrend = getRevenueTrend(dojangCode, targetMonth);
        
        // 4. 성별 분포
        GenderDistributionDto genderDistribution = getGenderDistribution(dojangCode);
        
        // 5. 학년별 제자수
        GradeDistributionDto gradeDistribution = getGradeDistribution(dojangCode);
        
        // 6. 패키지별 통계
        List<PackageStatDto> packageStats = getPackageStats(dojangCode, targetMonth);
        
        return DojangOperationRespDto.builder()
                .kpi(kpi)
                .studentTrend(studentTrend)
                .revenueTrend(revenueTrend)
                .genderDistribution(genderDistribution)
                .gradeDistribution(gradeDistribution)
                .packageStats(packageStats)
                .build();
    }

    /**
     * KPI 조회
     */
    private OperationKpiDto getKpi(String dojangCode, YearMonth targetMonth) {
        LocalDate endOfMonth = targetMonth.atEndOfMonth();
        String monthStr = targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // 해당 월말 기준 상태별 제자수 조회
        List<Object[]> statusCounts = studentStatusRepository.countByStatusAsOfDate(dojangCode, endOfMonth);
        
        Map<String, Integer> statusMap = new HashMap<>();
        for (Object[] row : statusCounts) {
            String statusCode = (String) row[0];
            Integer count = ((Number) row[1]).intValue();
            statusMap.put(statusCode, count);
        }
        
        Integer enrolled = statusMap.getOrDefault("재원", 0);
        Integer withdrawn = statusMap.getOrDefault("퇴관", 0);
        Integer trial = statusMap.getOrDefault("체험", 0);
        Integer totalStudents = enrolled + withdrawn + trial;
        
        // 재원율 계산
        Double retentionRate = totalStudents > 0 
                ? (enrolled.doubleValue() / totalStudents) * 100 
                : 0.0;
        
        // 이달의 매출 (납부완료만)
        Long monthRevenue = monthlyBillingRepository.sumRevenueByMonth(dojangCode, monthStr);
        if (monthRevenue == null) {
            monthRevenue = 0L;
        }
        
        return OperationKpiDto.builder()
                .totalStudents(totalStudents)
                .retentionRate(Math.round(retentionRate * 100.0) / 100.0)
                .monthRevenue(monthRevenue)
                .build();
    }

    /**
     * 월별 제자 추이 (6개월)
     */
    private List<MonthlyStudentTrendDto> getStudentTrend(String dojangCode, YearMonth targetMonth) {
        List<MonthlyStudentTrendDto> trends = new ArrayList<>();
        
        // 6개월 전부터 현재 월까지
        for (int i = 5; i >= 0; i--) {
            YearMonth month = targetMonth.minusMonths(i);
            LocalDate endOfMonth = month.atEndOfMonth();
            
            // 해당 월말 기준 상태별 제자수
            List<Object[]> statusCounts = studentStatusRepository.countByStatusAsOfDate(dojangCode, endOfMonth);
            
            Map<String, Integer> statusMap = new HashMap<>();
            for (Object[] row : statusCounts) {
                String statusCode = (String) row[0];
                Integer count = ((Number) row[1]).intValue();
                statusMap.put(statusCode, count);
            }
            
            trends.add(MonthlyStudentTrendDto.builder()
                    .month(month.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                    .enrolled(statusMap.getOrDefault("재원", 0))
                    .withdrawn(statusMap.getOrDefault("퇴관", 0))
                    .trial(statusMap.getOrDefault("체험", 0))
                    .build());
        }
        
        return trends;
    }

    /**
     * 월별 매출 추이 (6개월)
     */
    private List<MonthlyRevenueTrendDto> getRevenueTrend(String dojangCode, YearMonth targetMonth) {
        List<MonthlyRevenueTrendDto> trends = new ArrayList<>();
        
        // 6개월 전부터 현재 월까지
        for (int i = 5; i >= 0; i--) {
            YearMonth month = targetMonth.minusMonths(i);
            String monthStr = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            
            // 해당 월 매출 (납부완료만)
            Long revenue = monthlyBillingRepository.sumRevenueByMonth(dojangCode, monthStr);
            if (revenue == null) {
                revenue = 0L;
            }
            
            trends.add(MonthlyRevenueTrendDto.builder()
                    .month(monthStr)
                    .revenue(revenue)
                    .build());
        }
        
        return trends;
    }

    /**
     * 성별 분포 (gender_code는 Integer)
     */
    private GenderDistributionDto getGenderDistribution(String dojangCode) {
        List<Object[]> genderCounts = studentRepository.countByGender(dojangCode);
        
        Map<Integer, Integer> genderMap = new HashMap<>();
        for (Object[] row : genderCounts) {
            Integer genderCode = ((Number) row[0]).intValue();
            Integer count = ((Number) row[1]).intValue();
            genderMap.put(genderCode, count);
        }
        
        // gender_code: 1(남성), 2(여성), 3(기타)
        return GenderDistributionDto.builder()
                .male(genderMap.getOrDefault(1, 0))
                .female(genderMap.getOrDefault(2, 0))
                .other(genderMap.getOrDefault(3, 0))
                .build();
    }

    /**
     * 학년별 제자수 (AgeUtil 사용)
     */
    private GradeDistributionDto getGradeDistribution(String dojangCode) {
        // 모든 재원생의 생년월일 조회
        List<Object> birthDateObjects = studentRepository.findBirthDatesByDojang(dojangCode);
        
        int preschool = 0;  // 유아
        int elementary = 0; // 초등부 (초1~초6)
        int middle = 0;     // 중등부 (중1~중3)
        int high = 0;       // 고등부 (고1~고3)
        int adult = 0;      // 성인
        
        for (Object obj : birthDateObjects) {
            if (obj == null) continue;
            
            // java.sql.Date → LocalDate 변환
            LocalDate birthDate;
            if (obj instanceof java.sql.Date) {
                birthDate = ((java.sql.Date) obj).toLocalDate();
            } else if (obj instanceof LocalDate) {
                birthDate = (LocalDate) obj;
            } else {
                continue;
            }
            
            String grade = AgeUtil.calculateGrade(birthDate);
            
            if ("유아".equals(grade)) {
                preschool++;
            } else if (grade.startsWith("초")) {
                // 초1, 초2, 초3, 초4, 초5, 초6
                elementary++;
            } else if (grade.startsWith("중")) {
                // 중1, 중2, 중3
                middle++;
            } else if (grade.startsWith("고")) {
                // 고1, 고2, 고3
                high++;
            } else if ("성인".equals(grade)) {
                adult++;
            }
        }
        
        return GradeDistributionDto.builder()
                .preschool(preschool)
                .elementary(elementary)
                .middle(middle)
                .high(high)
                .adult(adult)
                .build();
    }

    /**
     * 패키지별 통계
     */
    private List<PackageStatDto> getPackageStats(String dojangCode, YearMonth targetMonth) {
        String monthStr = targetMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        List<Object[]> results = trainingMstRepository.findPackageStats(dojangCode, monthStr);
        
        List<PackageStatDto> stats = new ArrayList<>();
        for (Object[] row : results) {
            stats.add(PackageStatDto.builder()
                    .packageName((String) row[0])
                    .studentCount(((Number) row[1]).intValue())
                    .revenue(((Number) row[2]).longValue())
                    .build());
        }
        
        return stats;
    }
}