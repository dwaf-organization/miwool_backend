package com.taekwondo.miwool.dto.app.auth.reqDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginReqDto {
    
    private String dojangId; // 도장아이디
    private String dojangPw; // 도장비밀번호
}
 