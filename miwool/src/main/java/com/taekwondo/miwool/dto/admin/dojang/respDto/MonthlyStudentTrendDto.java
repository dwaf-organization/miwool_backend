package com.taekwondo.miwool.dto.admin.dojang.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStudentTrendDto {
    
    private String month; // 년월 (2026-04)
    private Integer enrolled; // 재원
    private Integer withdrawn; // 퇴관
    private Integer trial; // 체험
}