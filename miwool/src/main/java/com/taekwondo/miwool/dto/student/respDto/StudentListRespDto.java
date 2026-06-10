package com.taekwondo.miwool.dto.student.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentListRespDto {
    
    private String studentCode;    // 제자코드
    private String studentName;    // 제자명
    private Integer genderCode;    // 성별코드 (1=남, 2=여, 3=기타)
    private String genderName;     // 성별명
    private Integer age;           // 한국 나이
    private String grade;          // 학년 (초4, 중2 등)
    private String beltCode;       // 최신 급수 코드
    private String beltName;       // 최신 급수명
    private String ropeBeltCode;      // 줄넘기 급수 코드
    private String ropeBeltName;      // 줄넘기 급수명
    private LocalDate birthDate;   // 생년월일
    private String statusCode;     // 최신 재원상태 코드
    private LocalDate registDate;  // 입관일
    private LocalDate withdrawalDate;    // 퇴관일 (퇴관시만)
}