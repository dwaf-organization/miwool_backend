package com.taekwondo.miwool.dto.admin.dojang.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DojangDetailRespDto {
    
    private String dojangCode; // 도장코드
    private String dojangId; // 아이디
    private String dojangName; // 도장명
    private String masterName; // 관장명
    private LocalDate masterBirth; // 관장생년월일
    private String masterPhone; // 관장연락처
    private String dojangTel; // 도장연락처
    private String dojangEmail; // 이메일
    private String dojangBizNum; // 사업자번호
    private String dojangZipcode; // 우편번호
    private String dojangAdd; // 주소
    private String dojangAdd2; // 상세주소
    private String dojangStatus; // 운영상태
    private String selectedSchool; // 지정학교1
    private String selectedSchool2; // 지정학교2
    private String vipPackage; // VIP패키지
    private Integer isDeleted; // 삭제여부
    private LocalDateTime deletedAt; // 삭제일
    private String note; // 비고
    private Integer approvalYn; // 승인여부
    private LocalDateTime lastLoginAt; // 마지막로그인
    private LocalDateTime createdAt; // 생성일
    private LocalDateTime updatedAt; // 수정일
}