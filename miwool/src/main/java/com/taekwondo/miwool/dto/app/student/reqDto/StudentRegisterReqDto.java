package com.taekwondo.miwool.dto.app.student.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegisterReqDto {
    
    private String dojangCode; // 도장코드
    
    // 제자 정보
    private String studentName; // 제자명
    private String birthDate; // 생년월일 (YYYY-MM-DD)
    private Integer genderCode; // 성별코드 (1,2,3)
    private String genderName; // 성별기타
    private String studentPhone; // 연락처
    
    // 학교 정보
    private String schoolName; // 학교명
    private String grade; // 학년
    private String className; // 반/학급
    
    // 급수 정보
    private String beltCode; // 급수코드
    private Integer taekwondoMonths; // 태권도 경력(개월)
    
    // 보호자 정보
    private String guardianName; // 보호자명
    private String guardianRelationship; // 관계
    private String guardianPhone; // 보호자 연락처
}