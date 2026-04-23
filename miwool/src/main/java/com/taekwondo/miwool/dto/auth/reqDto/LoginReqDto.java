package com.taekwondo.miwool.dto.auth.reqDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginReqDto {
    
    @NotBlank(message = "아이디를 입력해주세요")
    private String dojangId;  // 아이디
    
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String dojangPw;  // 비밀번호
}