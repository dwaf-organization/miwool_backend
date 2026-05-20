package com.taekwondo.miwool.dto.admin.dashboard.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailedComparisonDto {
    
    private String dojangName; // 도장명
    private String masterName; // 관장명
    private Long studentCount; // 제자수
    private Double enrollmentRate; // 재원율 (%)
    private Long revenue; // 매출
    private Double monthOverMonthGrowth; // 전월대비 증감율 (%)
}