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
public class DailyRespDto {
    
    private String month; // 조회 월 (YYYYMM)
    private SummaryDto summary; // 월 요약 (달력과 동일)
    private List<DailyPaymentDto> dailyPayments; // 일별 납부 목록
    
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
    public static class DailyPaymentDto {
        private String date; // 날짜 (YYYY-MM-DD)
        private Integer totalAmount; // 일별 총액
        private List<PaymentDetailDto> payments; // 납부 상세 목록
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentDetailDto {
        private String studentName; // 제자명
        private Integer genderCode; // 성별코드 (1=남, 2=여)
        private Integer age; // 나이
        private String paymentMethod; // 납부방법
        private Integer paymentAmount; // 납부금액
    }
}