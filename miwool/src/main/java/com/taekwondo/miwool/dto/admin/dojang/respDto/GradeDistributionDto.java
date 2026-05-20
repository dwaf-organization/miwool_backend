package com.taekwondo.miwool.dto.admin.dojang.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeDistributionDto {
    
    private Integer preschool; // 유아
    private Integer elementary; // 초등부
    private Integer middle; // 중등부
    private Integer high; // 고등부
    private Integer adult; // 성인
}