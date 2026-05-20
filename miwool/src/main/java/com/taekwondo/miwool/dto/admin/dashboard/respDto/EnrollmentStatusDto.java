package com.taekwondo.miwool.dto.admin.dashboard.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentStatusDto {
    
    private String dojangName; // 도장명
    private Long enrolled; // 재원
    private Long withdrawn; // 퇴관
    private Long trial; // 체험
}