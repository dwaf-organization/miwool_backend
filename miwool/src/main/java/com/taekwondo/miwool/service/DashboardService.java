package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.dashboard.respDto.CalendarRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.CalendarRespDto.DailyDataDto;
import com.taekwondo.miwool.dto.dashboard.respDto.CalendarRespDto.SummaryDto;
import com.taekwondo.miwool.dto.dashboard.respDto.DailyRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.PopupRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.SummaryTabRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.WeeklyRespDto;
import com.taekwondo.miwool.repository.MonthlyBillingRepository;
import com.taekwondo.miwool.repository.StudentBeltRepository;
import com.taekwondo.miwool.repository.StudentRepository;
import com.taekwondo.miwool.repository.TuitionPaymentRepository;
import com.taekwondo.miwool.util.AgeUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudentRepository studentRepository;
    private final MonthlyBillingRepository monthlyBillingRepository;
    private final TuitionPaymentRepository tuitionPaymentRepository;
    private final StudentBeltRepository studentBeltRepository;

    @Transactional(readOnly = true)
    public CalendarRespDto getCalendarData(String dojangCode, String month) {
        log.info("달력 데이터 조회 시작: dojangCode={}, month={}", dojangCode, month);

        // 1. 월 요약 데이터 조회
        int totalEnrollment = studentRepository.countMonthlyEnrollment(dojangCode, month);
        int totalWithdrawal = studentRepository.countMonthlyWithdrawal(dojangCode, month);
        int currentTotal = studentRepository.countCurrentTotal(dojangCode, month);

        SummaryDto summary = SummaryDto.builder()
                .totalEnrollment(totalEnrollment)
                .totalWithdrawal(totalWithdrawal)
                .currentTotal(currentTotal)
                .build();

        // 2. 일별 학생 통계 조회 (입관/퇴관/체험)
        List<Object[]> studentStats = studentRepository.getDailyStudentStats(dojangCode, month);
        Map<String, DailyDataDto> dailyMap = new HashMap<>();

        for (Object[] row : studentStats) {
            Date sqlDate = (Date) row[0];
            String dateStr = sqlDate.toLocalDate().toString();
            
            int enrollment = ((Number) row[1]).intValue();
            int withdrawal = ((Number) row[2]).intValue();
            int trial = ((Number) row[3]).intValue();

            dailyMap.put(dateStr, DailyDataDto.builder()
                    .date(dateStr)
                    .enrollment(enrollment)
                    .withdrawal(withdrawal)
                    .trial(trial)
                    .paidAmount(0) // 초기값
                    .build());
        }

        // 4. 일별 승단예정 인원수 조회 ← 추가!
        List<Object[]> promoData = studentBeltRepository.getPromotionCountByMonth(dojangCode, month);
 
        for (Object[] row : promoData) {
            Date promoDateSql = (Date) row[0];
            String dateStr = promoDateSql.toLocalDate().toString();
            int promoCount = ((Number) row[1]).intValue();
 
            if (dailyMap.containsKey(dateStr)) {
                dailyMap.get(dateStr).setPromotion(promoCount);
            } else {
                dailyMap.put(dateStr, DailyDataDto.builder()
                        .date(dateStr)
                        .enrollment(0)
                        .withdrawal(0)
                        .trial(0)
                        .promotion(promoCount)
                        .paidAmount(0)
                        .build());
            }
        }

        // 3. 일별 납부완료 금액 조회
        List<Object[]> paidAmounts = monthlyBillingRepository.getDailyPaidAmount(dojangCode, month);

        for (Object[] row : paidAmounts) {
            Date sqlDate = (Date) row[0];
            String dateStr = sqlDate.toLocalDate().toString();
            int amount = ((Number) row[1]).intValue();

            // 해당 날짜 데이터가 있으면 금액 업데이트, 없으면 새로 생성
            if (dailyMap.containsKey(dateStr)) {
                dailyMap.get(dateStr).setPaidAmount(amount);
            } else {
                dailyMap.put(dateStr, DailyDataDto.builder()
                        .date(dateStr)
                        .enrollment(0)
                        .withdrawal(0)
                        .trial(0)
                        .paidAmount(amount)
                        .build());
            }
        }
        
        dailyMap.values().forEach(dto -> {
            if (dto.getPromotion() == null) dto.setPromotion(0);
        });
        
        // 5. Map을 List로 변환하고 날짜순 정렬
        List<DailyDataDto> dailyData = new ArrayList<>(dailyMap.values());
        dailyData.sort((a, b) -> a.getDate().compareTo(b.getDate()));

        log.info("달력 데이터 조회 완료: 총 {}일 데이터", dailyData.size());

        return CalendarRespDto.builder()
                .month(month)
                .summary(summary)
                .dailyData(dailyData)
                .build();
    }
    
    @Transactional(readOnly = true)
    public DailyRespDto getDailyPaymentData(String dojangCode, String month) {
        log.info("일일 납부 현황 조회 시작: dojangCode={}, month={}", dojangCode, month);
 
        // 1. 월 요약 데이터 조회 (달력과 동일)
        int totalEnrollment = studentRepository.countMonthlyEnrollment(dojangCode, month);
        int totalWithdrawal = studentRepository.countMonthlyWithdrawal(dojangCode, month);
        int currentTotal = studentRepository.countCurrentTotal(dojangCode, month);
 
        DailyRespDto.SummaryDto summary = DailyRespDto.SummaryDto.builder()
                .totalEnrollment(totalEnrollment)
                .totalWithdrawal(totalWithdrawal)
                .currentTotal(currentTotal)
                .build();
 
        // 2. 일별 납부 목록 조회
        List<Object[]> paymentList = tuitionPaymentRepository.getDailyPaymentList(dojangCode, month);
 
        // 3. 날짜별로 그룹핑
        Map<String, List<DailyRespDto.PaymentDetailDto>> dailyMap = new LinkedHashMap<>();
 
        for (Object[] row : paymentList) {
            Date paymentDateSql = (Date) row[0];
            String dateStr = paymentDateSql.toLocalDate().toString();
            String studentName = (String) row[1];
            Integer genderCode = (Integer) row[2];
            Date birthDateSql = (Date) row[3];
            LocalDate birthDate = birthDateSql.toLocalDate();
            String paymentMethod = (String) row[4];
            Integer paymentAmount = ((Number) row[5]).intValue();
 
            int age = AgeUtil.calculateKoreanAge(birthDate);
 
            DailyRespDto.PaymentDetailDto detail = DailyRespDto.PaymentDetailDto.builder()
                    .studentName(studentName)
                    .genderCode(genderCode)
                    .age(age)
                    .paymentMethod(paymentMethod)
                    .paymentAmount(paymentAmount)
                    .build();
 
            dailyMap.computeIfAbsent(dateStr, k -> new ArrayList<>()).add(detail);
        }
 
        // 4. DailyPaymentDto 리스트 생성
        List<DailyRespDto.DailyPaymentDto> dailyPayments = new ArrayList<>();
 
        for (Map.Entry<String, List<DailyRespDto.PaymentDetailDto>> entry : dailyMap.entrySet()) {
            String date = entry.getKey();
            List<DailyRespDto.PaymentDetailDto> payments = entry.getValue();
 
            // 일별 총액 계산
            int totalAmount = payments.stream()
                    .mapToInt(DailyRespDto.PaymentDetailDto::getPaymentAmount)
                    .sum();
 
            dailyPayments.add(DailyRespDto.DailyPaymentDto.builder()
                    .date(date)
                    .totalAmount(totalAmount)
                    .payments(payments)
                    .build());
        }
 
        log.info("일일 납부 현황 조회 완료: 총 {}일 데이터", dailyPayments.size());
 
        return DailyRespDto.builder()
                .month(month)
                .summary(summary)
                .dailyPayments(dailyPayments)
                .build();
    }

    @Transactional(readOnly = true)
    public WeeklyRespDto getWeeklyData(String dojangCode, String month) {
        log.info("주간 데이터 조회 시작: dojangCode={}, month={}", dojangCode, month);

        // 1. 월 요약 데이터 조회 (달력/일일과 동일)
        int totalEnrollment = studentRepository.countMonthlyEnrollment(dojangCode, month);
        int totalWithdrawal = studentRepository.countMonthlyWithdrawal(dojangCode, month);
        int currentTotal = studentRepository.countCurrentTotal(dojangCode, month);

        WeeklyRespDto.SummaryDto summary = WeeklyRespDto.SummaryDto.builder()
                .totalEnrollment(totalEnrollment)
                .totalWithdrawal(totalWithdrawal)
                .currentTotal(currentTotal)
                .build();

        // 2. 주차별 신규 입관 수 조회
        List<Object[]> enrollmentData = studentRepository.getWeeklyEnrollment(dojangCode, month);
        Map<String, Integer> enrollmentMap = new LinkedHashMap<>();

        for (Object[] row : enrollmentData) {
            Date weekStartSql = (Date) row[0];
            String weekStartStr = weekStartSql.toLocalDate().toString();
            int count = ((Number) row[1]).intValue();
            enrollmentMap.put(weekStartStr, count);
        }

        // 3. 주차별 매출 조회
        List<Object[]> revenueData = tuitionPaymentRepository.getWeeklyRevenue(dojangCode, month);
        Map<String, Integer> revenueMap = new LinkedHashMap<>();

        for (Object[] row : revenueData) {
            Date weekStartSql = (Date) row[0];
            String weekStartStr = weekStartSql.toLocalDate().toString();
            int revenue = ((Number) row[1]).intValue();
            revenueMap.put(weekStartStr, revenue);
        }

        // 4. 모든 주차 병합 (입관 + 매출)
        Set<String> allWeeks = new TreeSet<>();
        allWeeks.addAll(enrollmentMap.keySet());
        allWeeks.addAll(revenueMap.keySet());

        // 5. WeeklyDataDto 리스트 생성
        List<WeeklyRespDto.WeeklyDataDto> weeklyData = new ArrayList<>();

        for (String weekStartStr : allWeeks) {
            LocalDate weekStart = LocalDate.parse(weekStartStr);
            LocalDate weekEnd = weekStart.plusDays(6);

            // weekRange 형식: "4.5 ~ 4.11"
            String weekRange = String.format("%d.%d ~ %d.%d",
                    weekStart.getMonthValue(), weekStart.getDayOfMonth(),
                    weekEnd.getMonthValue(), weekEnd.getDayOfMonth());

            int newEnrollment = enrollmentMap.getOrDefault(weekStartStr, 0);
            int weeklyRevenue = revenueMap.getOrDefault(weekStartStr, 0);

            weeklyData.add(WeeklyRespDto.WeeklyDataDto.builder()
                    .weekRange(weekRange)
                    .newEnrollment(newEnrollment)
                    .weeklyRevenue(weeklyRevenue)
                    .build());
        }

        log.info("주간 데이터 조회 완료: 총 {}주 데이터", weeklyData.size());

        return WeeklyRespDto.builder()
                .month(month)
                .summary(summary)
                .weeklyData(weeklyData)
                .build();
    }

    @Transactional(readOnly = true)
    public SummaryTabRespDto getSummaryData(String dojangCode, String month) {
        log.info("요약 데이터 조회 시작: dojangCode={}, month={}", dojangCode, month);

        // 1. 전월 계산
        YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyyMM"));
        YearMonth previousYearMonth = yearMonth.minusMonths(1);
        String previousMonth = previousYearMonth.format(DateTimeFormatter.ofPattern("yyyyMM"));
        
        log.info("전월: {}", previousMonth);

        // 2. 월 요약 데이터 조회 (동일)
        int totalEnrollment = studentRepository.countMonthlyEnrollment(dojangCode, month);
        int totalWithdrawal = studentRepository.countMonthlyWithdrawal(dojangCode, month);
        int currentTotal = studentRepository.countCurrentTotal(dojangCode, month);

        SummaryTabRespDto.SummaryDto summary = SummaryTabRespDto.SummaryDto.builder()
                .totalEnrollment(totalEnrollment)
                .totalWithdrawal(totalWithdrawal)
                .currentTotal(currentTotal)
                .build();

        // 3. 입관 데이터 (명수만)
        int currentEnrollment = totalEnrollment;
        int previousEnrollment = studentRepository.countMonthlyEnrollment(dojangCode, previousMonth);
        
        SummaryTabRespDto.StudentCountDto enrollmentData = SummaryTabRespDto.StudentCountDto.builder()
                .current(currentEnrollment)
                .previous(previousEnrollment)
                .change(currentEnrollment - previousEnrollment)
                .build();

        // 4. 퇴관 데이터 (명수만)
        int currentWithdrawal = totalWithdrawal;
        int previousWithdrawal = studentRepository.countMonthlyWithdrawal(dojangCode, previousMonth);
        
        SummaryTabRespDto.StudentCountDto withdrawalData = SummaryTabRespDto.StudentCountDto.builder()
                .current(currentWithdrawal)
                .previous(previousWithdrawal)
                .change(currentWithdrawal - previousWithdrawal)
                .build();

        // 5. 체험 데이터 (명수만)
        int currentTrial = studentRepository.countMonthlyTrial(dojangCode, month);
        int previousTrial = studentRepository.countMonthlyTrial(dojangCode, previousMonth);
        
        SummaryTabRespDto.StudentCountDto trialData = SummaryTabRespDto.StudentCountDto.builder()
                .current(currentTrial)
                .previous(previousTrial)
                .change(currentTrial - previousTrial)
                .build();

        // 6. 매출 데이터 (율만)
        // billing_month 형식: "2026-04"
        String billingMonth = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String previousBillingMonth = previousYearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        int currentRevenue = monthlyBillingRepository.getMonthlyBillingTotal(dojangCode, billingMonth);
        int previousRevenue = monthlyBillingRepository.getMonthlyBillingTotal(dojangCode, previousBillingMonth);
        
        SummaryTabRespDto.RevenueDto revenueData = SummaryTabRespDto.RevenueDto.builder()
                .current(currentRevenue)
                .previous(previousRevenue)
                .changeRate(calculateChangeRate(currentRevenue, previousRevenue))
                .build();

        // 7. Details 구성
        SummaryTabRespDto.DetailsDto details = SummaryTabRespDto.DetailsDto.builder()
                .totalStudents(currentTotal)
                .enrollment(enrollmentData)
                .withdrawal(withdrawalData)
                .trial(trialData)
                .revenue(revenueData)
                .build();

        log.info("요약 데이터 조회 완료");

        return SummaryTabRespDto.builder()
                .month(month)
                .summary(summary)
                .details(details)
                .build();
    }

    /**
     * 증감율 계산 (매출용)
     * @param current 현재 값
     * @param previous 이전 값
     * @return 증감율 (%)
     */
    private Double calculateChangeRate(int current, int previous) {
        if (previous == 0) {
            if (current == 0) {
                return 0.0;
            } else {
                return 100.0; // 전월 0, 이번달 있음 → 100% 증가
            }
        }
        return Math.round(((double) (current - previous) / previous) * 100 * 100.0) / 100.0; // 소수점 2자리
    }
    
    /**
     * 일자 클릭시 팝업 서비스
     */
    @Transactional(readOnly = true)
    public PopupRespDto getPopupData(String dojangCode, String date) {
        log.info("달력 팝업 데이터 조회 시작: dojangCode={}, date={}", dojangCode, date);
 
        LocalDate localDate = LocalDate.parse(date);
 
        // 1. 입관 제자 목록
        List<Object[]> enrollmentRows = studentRepository.getStudentsByDateAndStatus(dojangCode, localDate, "재원");
        List<PopupRespDto.StudentInfoDto> enrollment = toStudentInfoDtoList(enrollmentRows);
 
        // 2. 퇴관 제자 목록
        List<Object[]> withdrawalRows = studentRepository.getStudentsByDateAndStatus(dojangCode, localDate, "퇴관");
        List<PopupRespDto.StudentInfoDto> withdrawal = toStudentInfoDtoList(withdrawalRows);
 
        // 3. 체험 제자 목록
        List<Object[]> trialRows = studentRepository.getStudentsByDateAndStatus(dojangCode, localDate, "체험");
        List<PopupRespDto.StudentInfoDto> trial = toStudentInfoDtoList(trialRows);
 
        // 4. 승단예정 제자 목록
        List<Object[]> promoRows = studentBeltRepository.getPromotionStudentsByDate(dojangCode, localDate);
        List<PopupRespDto.StudentInfoDto> promotion = promoRows.stream()
                .map(row -> {
                    String studentCode  = (String) row[0];
                    String studentName  = (String) row[1];
                    Integer genderCode  = (Integer) row[2];
                    LocalDate birthDate = ((java.sql.Date) row[3]).toLocalDate();
                    String beltCode     = (String) row[4];
                    String beltName     = (String) row[5];
                    String ropeBeltCode = (String) row[6];
                    String ropeBeltName = (String) row[7];
                    int age = AgeUtil.calculateKoreanAge(birthDate);
 
                    return PopupRespDto.StudentInfoDto.builder()
                            .studentCode(studentCode)
                            .studentName(studentName)
                            .genderCode(genderCode)
                            .age(age)
                            .beltCode(beltCode)
                            .beltName(beltName)
                            .ropeBeltCode(ropeBeltCode)
                            .ropeBeltName(ropeBeltName)
                            .build();
                })
                .collect(Collectors.toList());
 
        log.info("달력 팝업 데이터 조회 완료: date={}", date);
 
        return PopupRespDto.builder()
                .date(date)
                .enrollment(enrollment)
                .withdrawal(withdrawal)
                .trial(trial)
                .promotion(promotion)
                .build();
    }
 
    // 공통 변환 메서드
    private List<PopupRespDto.StudentInfoDto> toStudentInfoDtoList(List<Object[]> rows) {
        return rows.stream()
                .map(row -> {
                    String studentCode  = (String) row[0];
                    String studentName  = (String) row[1];
                    Integer genderCode  = (Integer) row[2];
                    LocalDate birthDate = ((java.sql.Date) row[3]).toLocalDate();
                    String beltCode     = (String) row[4];
                    String beltName     = (String) row[5];
                    String ropeBeltCode = (String) row[6];
                    String ropeBeltName = (String) row[7];
                    int age = AgeUtil.calculateKoreanAge(birthDate);
 
                    return PopupRespDto.StudentInfoDto.builder()
                            .studentCode(studentCode)
                            .studentName(studentName)
                            .genderCode(genderCode)
                            .age(age)
                            .beltCode(beltCode)
                            .beltName(beltName)
                            .ropeBeltCode(ropeBeltCode)
                            .ropeBeltName(ropeBeltName)
                            .build();
                })
                .collect(Collectors.toList());
    }
}