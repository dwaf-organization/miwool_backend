package com.taekwondo.miwool.dto.admin.dojang.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DojangOperationRespDto {
    
    private OperationKpiDto kpi; // KPI 카드
    private List<MonthlyStudentTrendDto> studentTrend; // 월별 제자 추이 (6개월)
    private List<MonthlyRevenueTrendDto> revenueTrend; // 월별 매출 추이 (6개월)
    private GenderDistributionDto genderDistribution; // 성별 분포
    private GradeDistributionDto gradeDistribution; // 학년별 제자수
    private List<PackageStatDto> packageStats; // 패키지별 통계
}