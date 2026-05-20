package com.taekwondo.miwool.dto.admin.dashboard.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpiDto {
    
    private Long pendingSignups; // 신규가입대기건수
    private Long totalDojangs; // 전체도장수
    private Long totalStudents; // 전체제자수
    private Long monthlyRevenue; // 이번달 총매출
}