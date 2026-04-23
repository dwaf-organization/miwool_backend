package com.taekwondo.miwool.dto.billing.reqDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillingListReqDto {
    
    private String studentSearch; // 제자 검색어 (제자명 또는 제자코드 부분 조회)
    private String billingStatus; // 납부 상태 (전체/미납/완납)
    private String startDate; // 청구일 시작 (yyyyMMdd)
    private String endDate; // 청구일 종료 (yyyyMMdd)
}