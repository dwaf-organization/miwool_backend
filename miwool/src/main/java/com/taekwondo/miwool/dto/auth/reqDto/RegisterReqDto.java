package com.taekwondo.miwool.dto.auth.reqDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterReqDto {
    
    // 필수 항목
    @NotBlank(message = "아이디는 필수입니다")
    @Pattern(regexp = "^[a-zA-Z0-9]{4,20}$", message = "아이디는 4~20자의 영문, 숫자만 가능합니다")
    private String dojangId;  // 아이디
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$", 
             message = "비밀번호는 8~20자의 영문, 숫자, 특수문자를 포함해야 합니다")
    private String dojangPw;  // 비밀번호
    
    @NotBlank(message = "도장명은 필수입니다")
    private String dojangName;  // 태권도장명
    
    @NotBlank(message = "관장명은 필수입니다")
    private String masterName;  // 관장명
    
    @NotNull(message = "관장 생년월일은 필수입니다")
    private LocalDate masterBirth;  // 관장생년월일
    
    // 선택 항목
    @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다")
    private String masterPhone;  // 관장연락처
    
    @Pattern(regexp = "^0[0-9]{1,2}-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다")
    private String dojangTel;  // 도장연락처
    
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String dojangEmail;  // 이메일
    
    private String dojangBizNum;  // 사업자등록번호
    
    private String dojangZipcode;  // 도장우편번호
    private String dojangAdd;  // 도장주소
    private String dojangAdd2;  // 도장상세주소
    
    private String selectedSchool;  // 지정학교1
    private String selectedSchool2;  // 지정학교2
    
    private String note;  // 비고
}