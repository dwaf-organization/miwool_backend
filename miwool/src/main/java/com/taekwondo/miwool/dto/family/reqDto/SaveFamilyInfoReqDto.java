package com.taekwondo.miwool.dto.family.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveFamilyInfoReqDto {
    
    @NotBlank(message = "제자코드는 필수입니다")
    private String studentCode;              // 제자코드 (필수)
    
    // student_family 정보
    private String familyComposition;        // 가족구성
    private String familyName;               // 가족이름
    private String familyBirth;              // 가족생년월일
    private Integer siblingCount;            // 형제수
    private String isAlsoStudent;            // 함께다니는제자
    private String primaryCaregiver;         // 주양육자
    private String familyNote;               // 가정특이사항메모
    
    // student_family_situation (가족특이사항)
    private List<String> familySituation;    // 특이사항코드 리스트
    private String familySituationEtc;       // 기타값 (999 코드)
    
    // student_education (부모교육가치)
    private List<String> educationValue;     // 교육가치코드 리스트
    private String educationValueEtc;        // 기타값 (999 코드)
}