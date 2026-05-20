package com.taekwondo.miwool.dto.app.auth.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawReqDto {
    
    private String dojangId; // 아이디
    private String dojangPw; // 비밀번호
}