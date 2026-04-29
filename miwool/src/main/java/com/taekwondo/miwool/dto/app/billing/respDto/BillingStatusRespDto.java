package com.taekwondo.miwool.dto.app.billing.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingStatusRespDto {
    
    // 상단 카드
    private String yearMonth; // 년월 (2026-04)
    private Integer completedCount; // 납부완료 인원수
    private Integer completedRevenue; // 납부완료 매출
    private Integer unpaidCount; // 미납 인원수
    private Integer unpaidRevenue; // 미납 매출
    
    // 제자 리스트
    private List<BillingStudentDto> students;
}