package com.taekwondo.miwool.dto.billing.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingListRespDto {
    
    private String studentCode; // 제자코드
    private Integer genderCode; // 성별
    private String studentName; // 제자명
    private Integer age; // 나이
    private String grade; // 학년
    private String beltCode; // 급수 코드
    private String beltName; // 급수명
    private Integer billingCode; // 청구코드
    private Integer billingAmount; // 청구금액
    private LocalDate billingDate; // 청구일
    private String billingStatus; // 납부상태
    private LocalDateTime paidAt; // 납부처리일
    private String paymentMethod; // 납부방법
    private Integer actualPaymentAmount; // 실제납부금액
    private String receiptPhone; // 현금영수증 전화번호
    private String note; // 납부금액 변경 사유
}