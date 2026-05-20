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
public class IntegratedConsultationRespDto {
    
    private CounselKpiDto counselKpi; // 상담 KPI 카드
    private List<CounselTrendDto> counselTrend; // 상담실행률 추이 (12개월)
    private List<DojangCounselDto> dojangCounselStatus; // 도장별 상담현황
    private List<CounselTypeDto> counselTypeStatistics; // 상담항목별 현황
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CounselKpiDto {
        private Integer totalCounselCount; // 총 상담건수
        private Integer totalCounseledStudents; // 총 상담제자수 (고유 학생)
        private Integer notCounseledStudents; // 미상담제자수
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CounselTrendDto {
        private String month; // 월 (2026-05)
        private Integer counselCount; // 상담건수
        private Double executionRate; // 실행율 (%)
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DojangCounselDto {
        private String dojangName; // 도장명
        private Integer totalStudents; // 전체 제자수
        private Integer counselCount; // 상담건수
        private Integer counseledStudents; // 상담제자수
        private Double executionRate; // 실행율 (%)
        private Integer notCounseledStudents; // 미상담제자수
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CounselTypeDto {
        private String counselType; // 상담유형 (대면상담, 전화상담, 문자상담)
        private Integer count; // 건수
        private Double percentage; // 전체 대비 비율 (%)
    }
}