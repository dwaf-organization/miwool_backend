package com.taekwondo.miwool.dto.auth.reqDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPwReqDto {
    
    @NotBlank(message = "아이디를 입력해주세요")
    private String dojangId;  // 아이디
    
    @NotBlank(message = "태권도장명을 입력해주세요")
    private String dojangName;  // 태권도장명
    
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String dojangEmail;  // 이메일
}