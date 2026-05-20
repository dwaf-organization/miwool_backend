package com.taekwondo.miwool.dto.admin.dashboard.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardRespDto {
    
    private KpiDto kpi; // KPI 카드
    private List<EducationRankingDto> educationRanking; // 교육관리 실행률 TOP 10
    private List<StudentRankingDto> studentRanking; // 제자수 TOP 5
    private List<EnrollmentStatusDto> enrollmentStatus; // 재원현황 TOP 5
    private List<RevenueComparisonDto> revenueComparison; // 매출비교 TOP 5
    private List<DetailedComparisonDto> detailedComparison; // 상세 비교표 TOP 5
}