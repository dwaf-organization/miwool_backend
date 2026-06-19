package com.taekwondo.miwool.dto.dashboard.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryTabRespDto {
    
    private String month; // 조회 월 (YYYYMM)
    private SummaryDto summary; // 월 요약 (동일)
    private DetailsDto details; // 상세 데이터
    
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
    public static class DetailsDto {
        private Integer totalStudents; // 전체 제자 수 (currentTotal과 동일)
        private StudentCountDto enrollment; // 입관 제자
        private StudentCountDto withdrawal; // 퇴관 제자
        private StudentCountDto trial; // 체험 제자
        private StudentCountDto suspension;    // 휴관 ← 추가!
        private StudentCountDto reinstatement; // 복관 ← 추가!
        private RevenueDto revenue; // 매출
    }
    
    // 재원 상태 - 명수만 표시
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentCountDto {
        private Integer current; // 이번달 수
        private Integer previous; // 전월 수
        private Integer change; // 증감 명수 (current - previous)
    }
    
    // 매출 - 율만 표시
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueDto {
        private Integer current; // 이번달 매출 예상
        private Integer previous; // 전월 매출
        private Double changeRate; // 증감율 (%)
    }
}