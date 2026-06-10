package com.taekwondo.miwool.dto.app.billing.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmPaymentReqDto {
    
    private Integer billingCode; // 청구코드
    private String paymentMethod; // 납부방법 (현금, 카드, 계좌이체 등)
    private Integer paidAmount; // 납부금액
    private LocalDate paidAt; // 납부일자
    private String receiptPhone;    // 현금영수증 전화번호
    private String note;            // 납부금액 변경 사유
}