package com.taekwondo.miwool.dto.training.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageOptionRespDto {
    
    private String packageCode;          // 패키지코드
    private String displayText;          // "주3회 정규반 / 주3회 / 150,000원"
    private String packageName;          // 패키지명
    private int weeklyCount;             // 주횟수
    private int basePrice;               // 기본교육비
}