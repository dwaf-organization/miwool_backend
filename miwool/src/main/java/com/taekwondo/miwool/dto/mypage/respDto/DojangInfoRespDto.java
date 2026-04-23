package com.taekwondo.miwool.dto.mypage.respDto;

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
public class DojangInfoRespDto {
    
    private String dojangCode;           // 태권도장고유코드
    private String dojangId;             // 아이디
    // dojangPw는 응답에 포함하지 않음
    
    private String dojangName;           // 태권도장명
    private String masterName;           // 관장명
    private LocalDate masterBirth;       // 관장생년월일
    private String masterPhone;          // 관장연락처
    private String dojangTel;            // 도장연락처
    private String dojangEmail;          // 이메일
    private String dojangBizNum;         // 사업자등록번호
    private String dojangZipcode;        // 도장우편번호
    private String dojangAdd;            // 도장주소
    private String dojangAdd2;           // 도장상세주소
    
    private String dojangStatus;         // 운영상태 (운영, 휴관, 폐관)
    private String selectedSchool;       // 지정학교1
    private String selectedSchool2;      // 지정학교2
    
    private int isDeleted;               // 삭제여부 (0=생성, 1=삭제)
    private LocalDateTime deletedAt;     // 삭제일
    private String note;                 // 비고
    private int approvalYn;              // 승인여부 (1=승인, 0=미승인)
    private LocalDateTime lastLoginAt;   // 마지막로그인일시
    
    private LocalDateTime createdAt;     // 생성일
    private LocalDateTime updatedAt;     // 수정일
}