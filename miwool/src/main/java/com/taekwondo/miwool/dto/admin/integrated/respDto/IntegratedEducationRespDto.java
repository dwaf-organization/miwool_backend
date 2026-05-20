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
public class IntegratedEducationRespDto {
    
    private OverallEducationDto overallEducation; // 전체교육지도현황
    private List<MonthlyEducationDto> monthlyTrend; // 월별 실행률 추이 (3개월)
    private List<DojangEducationDto> dojangEducation; // 도장별 교육지도 실행률
    private List<LowestItemDto> lowestByItem; // 항목별 최저 실행률 도장 (각 2개)
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverallEducationDto {
        private Double overallExecutionRate; // 종합실행률
        private List<ItemRateDto> itemExecutionRates; // 항목별 실행률
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemRateDto {
        private String itemName; // 항목명 (전화, 문자 등)
        private Double executionRate; // 실행률 (%)
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyEducationDto {
        private String month; // 월 (2026-05)
        private List<ItemRateDto> itemRates; // 항목별 실행률
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DojangEducationDto {
        private String dojangName; // 도장명
        private Integer totalStudents; // 전체 제자수
        private List<ItemRateDto> itemRates; // 항목별 실행률
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LowestItemDto {
        private String itemName; // 항목명
        private List<LowestDojangDto> lowestDojangs; // 최저 실행률 도장 2개
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LowestDojangDto {
        private String dojangName; // 도장명
        private Double executionRate; // 실행률 (%)
    }
}