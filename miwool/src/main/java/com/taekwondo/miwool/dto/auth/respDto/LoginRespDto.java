package com.taekwondo.miwool.dto.auth.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRespDto {
    
    private String dojangCode;  // 도장 고유 코드
    private String dojangId;  // 아이디
    private String dojangName;  // 도장명
    private String masterName;  // 관장명
    private Integer approvalYn;  // 승인여부 (1=승인, 0=미승인)
    private String dojangStatus;  // 운영상태 (운영, 휴관, 폐관)
    private String accessToken;  // Access Token
    private String refreshToken;  // Refresh Token
}