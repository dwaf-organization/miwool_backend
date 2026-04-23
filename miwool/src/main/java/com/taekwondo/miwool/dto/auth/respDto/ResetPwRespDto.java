package com.taekwondo.miwool.dto.auth.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPwRespDto {
    
    private String dojangId;  // 아이디
    private String tempPassword;  // 임시 비밀번호 (mw12345)
}