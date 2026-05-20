package com.taekwondo.miwool.dto.admin.dojang.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageStatDto {
    
    private String packageName; // 패키지명
    private Integer studentCount; // 수강중인 제자수
    private Long revenue; // 해당 월 매출
}