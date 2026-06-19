package com.taekwondo.miwool.dto.dashboard.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyRespDto {
    
    private String month; // 조회 월 (YYYYMM)
    private SummaryDto summary; // 월 요약 (달력/일일과 동일)
    private List<WeeklyDataDto> weeklyData; // 주차별 데이터
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryDto {
        private Integer totalEnrollment; // 해당 월 입관생 수
        private Integer totalSuspension;    // 휴관
        private Integer totalReinstatement; // 복관
        private Integer totalWithdrawal; // 해당 월 퇴관생 수
        private Integer currentTotal; // 현재 총원 (재원 + 체험)
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyDataDto {
        private String weekRange; // 주차 기간 (M.D ~ M.D)
        private Integer newEnrollment; // 신규 입관 수
        private Integer weeklyRevenue; // 주간 매출
    }
}