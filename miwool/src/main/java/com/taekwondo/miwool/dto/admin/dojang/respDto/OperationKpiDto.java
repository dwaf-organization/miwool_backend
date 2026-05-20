package com.taekwondo.miwool.dto.admin.dojang.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationKpiDto {
    
    private Integer totalStudents; // 전체 제자수
    private Double retentionRate; // 재원율 (%)
    private Long monthRevenue; // 이달의 매출
}