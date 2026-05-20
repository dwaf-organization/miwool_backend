package com.taekwondo.miwool.dto.admin.dojang.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VipPackageUpdateReqDto {
    
    private String dojangCode; // 도장코드
    private String vipPackage; // VIP패키지 (경영, 수업, 마스터)
}