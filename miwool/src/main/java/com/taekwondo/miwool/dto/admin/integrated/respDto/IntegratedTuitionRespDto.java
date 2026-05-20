package com.taekwondo.miwool.dto.admin.integrated.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegratedTuitionRespDto {
    
    private OverallSummaryDto overallSummary; // 전체매출현황
    private List<MonthlyRevenueDto> monthlyRevenueTrend; // 총매출추이 (12개월)
    private List<DojangRevenueDto> topDojangs; // 도장별매출현황 (상위 10개)
    private TuitionAnalysisDto tuitionAnalysis; // 평균교육비분석
    private List<WeeklyCountAnalysisDto> weeklyCountAnalysis; // 주횟수별평균교육비
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverallSummaryDto {
        private Integer totalRevenue; // 총매출 (만원)
        private Double revenueChangeRate; // 전월대비매출증감율 (%)
        private Integer averageTuitionFee; // 평균패키지교육비 (원)
        private Double tuitionChangeRate; // 전월대비평균교육비증감율 (%)
        private Integer totalDojangCount; // 총도장수
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyRevenueDto {
        private String month; // 월 (2026-05)
        private Integer revenue; // 매출 (만원)
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DojangRevenueDto {
        private String dojangName; // 도장명
        private Integer studentCount; // 제자수
        private Integer revenue; // 매출 (만원)
        private Integer averageTuitionFee; // 평균교육비 (원)
        private Double revenueChangeRate; // 매출증감율 (%)
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TuitionAnalysisDto {
        private Integer overallAverage; // 전체평균교육비 (원)
        private String highestDojangName; // 최고교육비도장명
        private Integer highestFee; // 최고교육비 (원)
        private String lowestDojangName; // 최저교육비도장명
        private Integer lowestFee; // 최저교육비 (원)
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyCountAnalysisDto {
        private Integer weeklyCount; // 주횟수 (1~7)
        private Integer averageFee; // 평균교육비 (원)
    }
}