package com.taekwondo.miwool.dto.admin.dojang.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DojangUpdateReqDto {
    
	private String dojangCode; // 도장코드
    private String dojangName; // 도장명
    private String masterName; // 관장명
    private LocalDate masterBirth; // 관장생년월일
    private String masterPhone; // 관장연락처
    private String dojangEmail; // 이메일
    private String dojangBizNum; // 사업자번호
    private String dojangZipcode; // 우편번호
    private String dojangAdd; // 주소
    private String dojangAdd2; // 상세주소
    private String dojangStatus; // 운영상태 (운영, 휴관, 폐관)
    private String selectedSchool; // 지정학교1
    private String selectedSchool2; // 지정학교2
    private String vipPackage; // VIP패키지
}