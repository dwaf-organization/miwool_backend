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
public class PaymentMethodRevenueRespDto {
    
    private String month;                              // 조회 월 (yyyyMM)
    private Integer totalAmount;                       // 총 납부금액
    private List<PaymentMethodDto> paymentMethodRevenues; // 납부방법별 금액
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMethodDto {
        private String paymentMethod; // 납부방법
        private Integer amount;       // 납부금액
    }
}