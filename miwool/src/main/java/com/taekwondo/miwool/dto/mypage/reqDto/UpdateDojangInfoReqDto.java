package com.taekwondo.miwool.dto.mypage.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDojangInfoReqDto {
    
    // dojangCode, dojangId는 수정 불가 (토큰에서 가져옴)
    
    private String dojangPw;             // 비밀번호 (null/빈값이면 수정 안 함)
    
    @NotBlank(message = "태권도장명은 필수입니다")
    private String dojangName;           // 태권도장명
    
    @NotBlank(message = "관장명은 필수입니다")
    private String masterName;           // 관장명
    
    @NotNull(message = "관장생년월일은 필수입니다")
    private LocalDate masterBirth;       // 관장생년월일
    
    private String masterPhone;          // 관장연락처
    private String dojangTel;            // 도장연락처
    private String dojangEmail;          // 이메일
    private String dojangBizNum;         // 사업자등록번호
    private String dojangZipcode;        // 도장우편번호
    private String dojangAdd;            // 도장주소
    private String dojangAdd2;           // 도장상세주소
    private String dojangStatus;		 //	도장운영상태
    
    private String selectedSchool;       // 지정학교1
    private String selectedSchool2;      // 지정학교2
    
    private String note;                 // 비고
}