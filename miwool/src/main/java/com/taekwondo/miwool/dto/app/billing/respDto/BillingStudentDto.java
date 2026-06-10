package com.taekwondo.miwool.dto.app.billing.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingStudentDto {
    
    private Integer billingCode; // 청구코드 (납부처리 시 필요)
    private Integer genderCode; // 성별코드
    private String studentName; // 제자명
    private Integer age; // 나이
    private String grade; // 학년
    private Integer billingAmount; // 청구금액
    private LocalDate billingDate; // 청구일
    private String billingStatus; // 납부상태 (납부완료, 미납)
    private String paymentMethod;         // 납부방법
    private Integer actualPaymentAmount;  // 실제납부금액
    private String receiptPhone;          // 현금영수증 전화번호
    private String note;                  // 납부금액 변경 사유
}