package com.taekwondo.miwool.dto.student.reqDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterStudentReqDto {
    
    // ===== 제자 정보 =====
    @NotBlank(message = "제자명은 필수입니다")
    private String studentName;  // 제자명
    
    private String studentNameEn;  // 영문이름
    
    private String profileImage;             // 이미지 URL
    
    @NotNull(message = "입관일은 필수입니다")
    private LocalDate registDate;  // 입관일
    
    @NotBlank(message = "급수는 필수입니다")
    private String beltCode;  // 급수 (공통코드)
    
    private String ropeBeltCode;  // 줄넘기 급수 (공통코드)
    
    @NotNull(message = "생년월일은 필수입니다")
    private LocalDate birthDate;  // 생년월일
    
    @NotNull(message = "성별은 필수입니다")
    private Integer genderCode;  // 성별 (1=남, 2=여, 3=기타)
    
    private String genderName;  // 성별기타
    
    private String studentPhone;  // 연락처
    
    private String studentZipcode;  // 우편번호
    private String studentAdd;  // 주소
    private String studentAdd2;  // 상세주소
    
    private String schoolName;  // 학교명
    
    private String grade;  // 학년
    private String className;  // 반/학급
    
    private Integer hasExerciseHistory;  // 운동경력여부 (1=있음, 0=없음)
    private Integer taekwondoMonths;  // 태권도 경력 (개월수) - student_belt에 저장
    private String previousSports;  // 종목명 - student_mst에 저장
    private String previousDojangExp;  // 이전도장경험
    
    private String registPathCode;  // 등록경로 (직접 입력: 소개, 전단, 블로그, 인스타, 재등록, 기타)
    private List<String> purposeCodes;  // 등록목적 (공통코드 리스트)
    private String purposeEtcValue;  // 등록목적 기타값 (PURPOSE_009 선택 시 입력)
    private String registReason;  // 등록사유상세
    
    private Integer hasMedication;  // 복용약여부
    private Integer hasAllergy;  // 알레르기여부
    private Integer hasSurgery;  // 수술여부 (1=있음, 0=없음)
    private String healthNote;  // 건강관련메모
    
    // ===== 보호자 정보 =====
    @NotBlank(message = "보호자명은 필수입니다")
    private String guardianName;  // 보호자명
    
    @NotBlank(message = "보호자 관계는 필수입니다")
    private String guardianRelationship;  // 보호자관계
    
    @NotBlank(message = "보호자 연락처는 필수입니다")
    private String guardianPhone;  // 보호자연락처
    
    private String guardianEmergencyPhone;  // 비상연락처
    private LocalDate guardianBirthDate;  // 보호자생년월일
    private String guardianJob;  // 직업
    private LocalDate guardianAnniversaryDate;  // 결혼기념일
    private String guardianRequest;  // 보호자요청사항
}