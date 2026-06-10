package com.taekwondo.miwool.dto.common.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSelectionRespDto {
    
    private String studentCode;        // 제자코드
    private int genderCode;            // 성별코드 (1=남, 2=여, 3=기타)
    private String studentName;        // 제자명
    private int age;                   // 나이 (한국나이)
    private String grade;              // 학년 (초1, 초2, 초3, ...)
    private String beltCode;           // 급수코드
    private String beltName; // 급수명
    private String ropeBeltCode;       // 줄넘기 급수코드
    private String ropeBeltName;       // 줄넘기 급수명
}