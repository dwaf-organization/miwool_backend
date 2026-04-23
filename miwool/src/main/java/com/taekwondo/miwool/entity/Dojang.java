package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "taekwondo_mst")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Dojang {
    
    @Id
    @Column(name = "dojang_code", length = 20, nullable = false)
    private String dojangCode;  // 태권도장고유코드 (자동생성: DJ+YYYYMMDD+seq)
    
    @Column(name = "dojang_id", length = 50, nullable = false, unique = true)
    private String dojangId;  // 아이디
    
    @Column(name = "dojang_pw", nullable = false)
    private String dojangPw;  // 비밀번호 (BCrypt 암호화)
    
    @Column(name = "dojang_name", length = 100, nullable = false)
    private String dojangName;  // 태권도장명
    
    @Column(name = "master_name", length = 50, nullable = false)
    private String masterName;  // 관장명
    
    @Column(name = "master_birth", nullable = false)
    private LocalDate masterBirth;  // 관장생년월일
    
    @Column(name = "master_phone", length = 20)
    private String masterPhone;  // 관장연락처
    
    @Column(name = "dojang_tel", length = 20)
    private String dojangTel;  // 도장연락처
    
    @Column(name = "dojang_email", length = 100)
    private String dojangEmail;  // 이메일
    
    @Column(name = "dojang_biz_num", length = 50)
    private String dojangBizNum;  // 사업자등록번호
    
    @Column(name = "dojang_zipcode", length = 10)
    private String dojangZipcode;  // 도장우편번호
    
    @Column(name = "dojang_add", length = 200)
    private String dojangAdd;  // 도장주소
    
    @Column(name = "dojang_add2", length = 200)
    private String dojangAdd2;  // 도장상세주소
    
    @Column(name = "dojang_status", length = 10, nullable = false)
    @Builder.Default
    private String dojangStatus = "운영";  // 운영상태 (운영, 휴관, 폐관)
    
    @Column(name = "selected_school", length = 50)
    private String selectedSchool;  // 지정학교1
    
    @Column(name = "selected_school2", length = 50)
    private String selectedSchool2;  // 지정학교2
    
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Integer isDeleted = 0;  // 삭제여부 (0=생성, 1=삭제)
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;  // 삭제일
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;  // 비고
    
    @Column(name = "approval_yn")
    @Builder.Default
    private Integer approvalYn = 0;  // 승인여부 (1=승인, 0=미승인)
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;  // 마지막로그인일시
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성일
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;  // 수정일
}