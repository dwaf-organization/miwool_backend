package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_mst")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Student {
    
    @Id
    @Column(name = "student_code", length = 20, nullable = false)
    private String studentCode;  // 제자고유코드 (자동생성)
    
    @Column(name = "dojang_code", length = 50, nullable = false)
    private String dojangCode;  // 태권도장고유코드
    
    @Column(name = "student_name", length = 100, nullable = false)
    private String studentName;  // 제자명
    
    @Column(name = "student_name_en", length = 100)
    private String studentNameEn;  // 영문명
    
    @Column(name = "student_zipcode", length = 10)
    private String studentZipcode;  // 우편번호
    
    @Column(name = "student_add", length = 200)
    private String studentAdd;  // 주소
    
    @Column(name = "student_add2", length = 200)
    private String studentAdd2;  // 상세주소
    
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;  // 생년월일
    
    @Column(name = "gender_code", nullable = false)
    private Integer genderCode;  // 성별코드 (1=남, 2=여, 3=기타)
    
    @Column(name = "gender_name", length = 50)
    private String genderName;  // 성별이름 (기타시 입력)
    
    @Column(name = "student_phone", length = 20)
    private String studentPhone;  // 연락처
    
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;  // 프로필사진
    
    @Column(name = "school_name", length = 100, nullable = false)
    private String schoolName;  // 학교명
    
    @Column(name = "grade", length = 10)
    private String grade;  // 학년
    
    @Column(name = "class_name", length = 20)
    private String className;  // 반/학급
    
    @Column(name = "status_code", length = 20, nullable = false)
    private String statusCode;  // 재원상태코드
    
    @Column(name = "belt_code", length = 20)
    private String beltCode;
    
    @Column(name = "rope_belt_code", length = 20)
    private String ropeBeltCode;  // 현재 줄넘기 띠
    
    @Column(name = "regist_date", nullable = false)
    private LocalDate registDate;  // 입관일
    
    @Column(name = "regist_path_code", length = 20)
    private String registPathCode;  // 등록경로코드 (공통코드)
    
    @Column(name = "regist_reason", columnDefinition = "TEXT")
    private String registReason;  // 등록사유상세
    
    @Column(name = "has_exercise_history")
    private Integer hasExerciseHistory;  // 운동경력유무 (1=있음, 0=없음)
    
    @Column(name = "previous_sports", length = 50)
    private String previousSports;  // 종목명
    
    @Column(name = "previous_dojang_exp", columnDefinition = "TEXT")
    private String previousDojangExp;  // 이전도장경험
    
    @Column(name = "health_note", columnDefinition = "TEXT")
    private String healthNote;  // 건강관련메모
    
    @Column(name = "has_medication")
    private Integer hasMedication;  // 복용약여부
    
    @Column(name = "has_allergy")
    private Integer hasAllergy;  // 알레르기여부
    
    @Column(name = "has_surgery")
    private Integer hasSurgery;  // 수술여부 (1=있음, 0=없음)
    
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Integer isDeleted = 0;  // 삭제여부 (0=생성, 1=삭제)
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;  // 삭제일
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성일
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;  // 수정일
}