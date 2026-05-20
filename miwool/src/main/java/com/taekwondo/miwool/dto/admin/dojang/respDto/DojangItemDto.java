package com.taekwondo.miwool.dto.admin.dojang.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DojangItemDto {
    
    private String dojangCode; // 도장코드
    private String dojangName; // 도장명
    private String masterName; // 관장명
    private String masterPhone; // 관장연락처
    private String dojangTel; // 도장연락처
    private String dojangAddress; // 도장주소 (주소 + 상세주소)
    private String dojangStatus; // 운영상태
    private Integer approvalYn; // 승인여부
    private String vipPackage; // VIP패키지
}