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
public class CalendarRespDto {
    
    private String month; // 조회 월 (YYYY-MM)
    private SummaryDto summary; // 월 요약
    private List<DailyDataDto> dailyData; // 일별 데이터
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryDto {
        private Integer totalEnrollment; // 해당 월 입관생 수
        private Integer totalWithdrawal; // 해당 월 퇴관생 수
        private Integer currentTotal; // 현재 총원 (재원 + 체험)
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyDataDto {
        private String date; // 날짜 (YYYY-MM-DD)
        private Integer enrollment; // 입관 +n
        private Integer withdrawal; // 퇴관 +n
        private Integer trial; // 체험 +n
        private Integer promotion; // 승단예정 +n
        private Integer paidAmount; // 납부완료 금액
    }
}