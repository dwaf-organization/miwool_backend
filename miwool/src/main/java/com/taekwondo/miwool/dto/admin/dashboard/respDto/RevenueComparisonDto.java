package com.taekwondo.miwool.dto.admin.dashboard.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueComparisonDto {
    
    private String dojangName; // 도장명
    private Long revenue; // 매출
}