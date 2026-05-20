package com.taekwondo.miwool.service.admin;

import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedTuitionRespDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedTuitionRespDto.DojangRevenueDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedTuitionRespDto.MonthlyRevenueDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedTuitionRespDto.OverallSummaryDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedTuitionRespDto.TuitionAnalysisDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedTuitionRespDto.WeeklyCountAnalysisDto;
import com.taekwondo.miwool.repository.DojangRepository;
import com.taekwondo.miwool.repository.TuitionPaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class AdminIntegratedTuitionService {
    
    @Autowired
    private TuitionPaymentRepository tuitionPaymentRepository;
    
    @Autowired
    private DojangRepository dojangRepository;
    
    @Transactional(readOnly = true)
    public IntegratedTuitionRespDto getIntegratedTuition(String month) {
        log.info("교육비 통합 데이터 조회 시작: month={}", month);
        
        // 1. 전체매출현황
        OverallSummaryDto overallSummary = getOverallSummary(month);
        
        // 2. 총매출추이 (12개월)
        List<MonthlyRevenueDto> monthlyRevenueTrend = getMonthlyRevenueTrend(month);
        
        // 3. 도장별매출현황 (상위 10개)
        List<DojangRevenueDto> topDojangs = getTopDojangs(month);
        
        // 4. 평균교육비분석
        TuitionAnalysisDto tuitionAnalysis = getTuitionAnalysis(month);
        
        // 5. 주횟수별평균교육비
        List<WeeklyCountAnalysisDto> weeklyCountAnalysis = getWeeklyCountAnalysis(month);
        
        log.info("교육비 통합 데이터 조회 완료: month={}", month);
        
        return IntegratedTuitionRespDto.builder()
                .overallSummary(overallSummary)
                .monthlyRevenueTrend(monthlyRevenueTrend)
                .topDojangs(topDojangs)
                .tuitionAnalysis(tuitionAnalysis)
                .weeklyCountAnalysis(weeklyCountAnalysis)
                .build();
    }
    
    // 1. 전체매출현황
    private OverallSummaryDto getOverallSummary(String month) {
        // 해당 월 총 매출 (만원)
        Integer totalRevenue = tuitionPaymentRepository.getTotalRevenue(month);
        if (totalRevenue == null) totalRevenue = 0;
        
        // 전월 계산
        String prevMonth = getPreviousMonth(month);
        Integer prevRevenue = tuitionPaymentRepository.getTotalRevenue(prevMonth);
        if (prevRevenue == null) prevRevenue = 0;
        
        // 매출 증감율
        double revenueChangeRate = 0.0;
        if (prevRevenue > 0) {
            revenueChangeRate = ((totalRevenue - prevRevenue) * 100.0 / prevRevenue);
            revenueChangeRate = Math.round(revenueChangeRate * 10.0) / 10.0;
        }
        
        // 평균 교육비
        Integer averageTuitionFee = tuitionPaymentRepository.getOverallAverageFee(month);
        if (averageTuitionFee == null) averageTuitionFee = 0;
        
        Integer prevAverageFee = tuitionPaymentRepository.getOverallAverageFee(prevMonth);
        if (prevAverageFee == null) prevAverageFee = 0;
        
        // 교육비 증감율
        double tuitionChangeRate = 0.0;
        if (prevAverageFee > 0) {
            tuitionChangeRate = ((averageTuitionFee - prevAverageFee) * 100.0 / prevAverageFee);
            tuitionChangeRate = Math.round(tuitionChangeRate * 10.0) / 10.0;
        }
        
        // 총 도장수 (운영중, 승인된 도장)
        int totalDojangCount = (int) dojangRepository.count();
        
        return OverallSummaryDto.builder()
                .totalRevenue(totalRevenue)
                .revenueChangeRate(revenueChangeRate)
                .averageTuitionFee(averageTuitionFee)
                .tuitionChangeRate(tuitionChangeRate)
                .totalDojangCount(totalDojangCount)
                .build();
    }
    
    // 2. 총매출추이 (12개월)
    private List<MonthlyRevenueDto> getMonthlyRevenueTrend(String month) {
        // DB에서 조회된 데이터
        List<Object[]> trendData = tuitionPaymentRepository.getMonthlyRevenueTrend(month);
        
        // Map으로 변환 (month -> revenue)
        Map<String, Integer> revenueMap = new HashMap<>();
        for (Object[] row : trendData) {
            String monthStr = (String) row[0];
            Integer revenue = row[1] != null ? ((Number) row[1]).intValue() : 0;
            revenueMap.put(monthStr, revenue);
        }
        
        // 12개월 전부터 현재까지 모든 월 생성
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate endDate = LocalDate.parse(month + "-01");
        LocalDate startDate = endDate.minusMonths(11);
        
        List<MonthlyRevenueDto> result = new ArrayList<>();
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            String monthStr = current.format(formatter);
            Integer revenue = revenueMap.getOrDefault(monthStr, 0);
            
            result.add(MonthlyRevenueDto.builder()
                    .month(monthStr)
                    .revenue(revenue)
                    .build());
            
            current = current.plusMonths(1);
        }
        
        return result;
    }
    
    // 3. 도장별매출현황 (상위 10개)
    private List<DojangRevenueDto> getTopDojangs(String month) {
        // (dojang_code, dojang_name, student_count, revenue, avg_fee)
        List<Object[]> dojangData = tuitionPaymentRepository.getTopDojangsByRevenue(month);
        
        // 전월 데이터 (증감율 계산용)
        String prevMonth = getPreviousMonth(month);
        List<Object[]> prevDojangData = tuitionPaymentRepository.getTopDojangsByRevenue(prevMonth);
        
        // 전월 매출 Map (dojang_code -> revenue)
        Map<String, Integer> prevRevenueMap = new HashMap<>();
        for (Object[] row : prevDojangData) {
            String dojangCode = (String) row[0];
            Integer revenue = row[3] != null ? ((Number) row[3]).intValue() : 0;
            prevRevenueMap.put(dojangCode, revenue);
        }
        
        return dojangData.stream()
                .map(row -> {
                    String dojangCode = (String) row[0];
                    String dojangName = (String) row[1];
                    Integer studentCount = ((Number) row[2]).intValue();
                    Integer revenue = row[3] != null ? ((Number) row[3]).intValue() : 0;
                    Integer averageTuitionFee = row[4] != null ? ((Number) row[4]).intValue() : 0;
                    
                    // 증감율 계산
                    double revenueChangeRate = 0.0;
                    Integer prevRevenue = prevRevenueMap.get(dojangCode);
                    if (prevRevenue != null && prevRevenue > 0) {
                        revenueChangeRate = ((revenue - prevRevenue) * 100.0 / prevRevenue);
                        revenueChangeRate = Math.round(revenueChangeRate * 10.0) / 10.0;
                    }
                    
                    return DojangRevenueDto.builder()
                            .dojangName(dojangName)
                            .studentCount(studentCount)
                            .revenue(revenue)
                            .averageTuitionFee(averageTuitionFee)
                            .revenueChangeRate(revenueChangeRate)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    // 4. 평균교육비분석
    private TuitionAnalysisDto getTuitionAnalysis(String month) {
        // 전체 평균
        Integer overallAverage = tuitionPaymentRepository.getOverallAverageFee(month);
        if (overallAverage == null) overallAverage = 0;
        
        // 도장별 평균 (dojang_name, avg_fee)
        List<Object[]> dojangFees = tuitionPaymentRepository.getDojangAverageFees(month);
        
        String highestDojangName = "";
        Integer highestFee = 0;
        String lowestDojangName = "";
        Integer lowestFee = 0;
        
        if (!dojangFees.isEmpty()) {
            // 첫 번째가 최고
            Object[] highest = dojangFees.get(0);
            highestDojangName = (String) highest[0];
            highestFee = ((Number) highest[1]).intValue();
            
            // 마지막이 최저
            Object[] lowest = dojangFees.get(dojangFees.size() - 1);
            lowestDojangName = (String) lowest[0];
            lowestFee = ((Number) lowest[1]).intValue();
        }
        
        return TuitionAnalysisDto.builder()
                .overallAverage(overallAverage)
                .highestDojangName(highestDojangName)
                .highestFee(highestFee)
                .lowestDojangName(lowestDojangName)
                .lowestFee(lowestFee)
                .build();
    }
    
    // 5. 주횟수별평균교육비
    private List<WeeklyCountAnalysisDto> getWeeklyCountAnalysis(String month) {
        // (weekly_count, avg_fee)
        List<Object[]> weeklyData = tuitionPaymentRepository.getAverageFeeByWeeklyCount(month);
        
        // Map으로 변환
        Map<Integer, Integer> weeklyFeeMap = new HashMap<>();
        for (Object[] row : weeklyData) {
            Integer weeklyCount = ((Number) row[0]).intValue();
            Integer avgFee = row[1] != null ? ((Number) row[1]).intValue() : 0;
            weeklyFeeMap.put(weeklyCount, avgFee);
        }
        
        // 1~7까지 모두 생성 (없으면 0)
        return IntStream.rangeClosed(1, 7)
                .mapToObj(count -> WeeklyCountAnalysisDto.builder()
                        .weeklyCount(count)
                        .averageFee(weeklyFeeMap.getOrDefault(count, 0))
                        .build())
                .collect(Collectors.toList());
    }
    
    // 전월 계산 (2026-05 → 2026-04)
    private String getPreviousMonth(String month) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate date = LocalDate.parse(month + "-01");
        LocalDate prevDate = date.minusMonths(1);
        return prevDate.format(formatter);
    }
}