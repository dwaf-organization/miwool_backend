package com.taekwondo.miwool.dto.student.respDto;

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
public class StudentBasicInfoRespDto {
    
    // === 기본 정보 (읽기 전용) ===
    private String studentCode;              // 제자코드 (수정 불가)
    
    // === 제자 기본 정보 (수정 가능) ===
    private String studentName;              // 제자명
    private String studentNameEn;            // 영문명
    private LocalDate registDate;            // 입관일
    private String statusCode;               // 재원상태
    private String beltCode;                 // 급수
    private String ropeBeltCode;             // 줄넘기 급수
    private String profileImage;             // 이미지 URL
    
    // === 개인 정보 (수정 가능) ===
    private LocalDate birthDate;             // 생년월일
    private Integer genderCode;              // 성별 (1=남, 2=여, 3=기타)
    private String genderName;               // 성별명 (기타일 경우)
    private String studentPhone;             // 제자연락처
    
    // === 주소 정보 (수정 가능) ===
    private String studentZipcode;           // 우편번호
    private String studentAdd;               // 주소
    private String studentAdd2;              // 상세주소
    
    // === 학교 정보 (수정 가능) ===
    private String schoolName;               // 학교명
    private String grade;                    // 학년
    private String className;                // 반/학급
    
    // === 등록 정보 (수정 가능) ===
    private String registPathCode;           // 등록경로
    private List<String> purposeCodes;       // 등록목적 (다중)
    private String purposeEtcValue;          // 등록목적 기타값
    private String registReason;             // 등록사유상세
    
    // === 운동 경력 (수정 가능) ===
    private Integer hasExerciseHistory;      // 운동경력유무 (0=무, 1=유)
    private Integer taekwondoMonths;         // 태권도 개월수
    private String previousSports;           // 이전운동종목
    private String previousDojangExp;        // 이전도장경험
    
    // === 건강 정보 (수정 가능) ===
    private Integer hasMedication;           // 복용약여부 (0=무, 1=유)
    private Integer hasAllergy;              // 알레르기여부 (0=무, 1=유)
    private Integer hasSurgery;              // 수술여부 (0=무, 1=유)
    private String healthNote;               // 건강메모
    
    // === 보호자 정보 (읽기 전용) ===
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuardianInfo {
        private String guardianCode;         // 보호자코드
        private String guardianName;         // 보호자명
        private String relationship;         // 보호자관계
        private String guardianPhone;        // 보호자연락처
        private LocalDate guardianBirthDate; // 보호자생년월일
    }
    
    private GuardianInfo guardian;           // 주 보호자 정보
}