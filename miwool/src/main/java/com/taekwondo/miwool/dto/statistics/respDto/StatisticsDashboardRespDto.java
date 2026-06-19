package com.taekwondo.miwool.dto.statistics.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDashboardRespDto {
    
    private String month; // 조회 월 (YYYYMM)
    private List<MonthlyStatusDto> monthlyStatus; // 월별 재원현황 (12개월)
    private List<PackageDistributionDto> packageDistribution; // 패키지분포
    private List<CodeCountDto> enrollPurpose; // 등록목적
    private CharacteristicsDto characteristics; // 성향/특성 (6개 항목)
    private List<CounselStatsDto> counselStats; // 상담통계
    private List<EducationStatsDto> educationStats; // 교육통계
    
    // 월별 재원현황 (12개월)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyStatusDto {
        private String month; // 월 (YYYYMM)
        private Integer all; // 재원생수
        private Integer enrolled; // 입관 수
        private Integer trial; // 체험 수
        private Integer withdrawn; // 퇴관 수
        private Integer suspended;    // 휴관 수
        private Integer reinstated;   // 복관 수
    }
    
    // 패키지분포
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackageDistributionDto {
        private String packageCode; // 패키지코드
        private String packageName; // 패키지명
        private Integer studentCount; // 인원수
    }
    
    // 공통 코드별 카운트 (등록목적, 성향 등)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeCountDto {
        private String code; // 코드
        private String name; // 코드명
        private Integer count; // 인원수
    }
    
    // 성향/특성 묶음
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CharacteristicsDto {
        private List<CodeCountDto> baseCharacter; // 기본성향
        private List<CodeCountDto> emotion; // 정서특성
        private List<CodeCountDto> social; // 사회성특성
        private List<CodeCountDto> classResponse; // 수업반응특성
        private List<CodeCountDto> improvement; // 변화필요부분
        private List<CodeCountDto> strength; // 강점
        private List<CodeCountDto> skill; // 기능습득속도
    }
    
    // 상담통계
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CounselStatsDto {
        private String counselType; // 상담유형
        private Integer count; // 건수
    }
    
    // 교육통계
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EducationStatsDto {
        private String managementType; // 교육지도 유형
        private Integer completedCount; // 실시 완료 제자 수
        private Integer totalCount; // 전체 재원생 수
        private Double completionRate; // 실시율 (%)
    }
}