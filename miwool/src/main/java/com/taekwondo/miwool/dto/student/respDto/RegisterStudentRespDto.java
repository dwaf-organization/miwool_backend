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
public class RegisterStudentRespDto {
    
    private String studentCode;  // 제자 고유 코드
    private String studentName;  // 제자명
    private LocalDate registDate;  // 입관일
    private String beltCode;  // 급수
    private String statusCode;  // 재원상태 (재원)
    private String guardianCode;  // 보호자 코드
    private String guardianName;  // 보호자명
    private String guardianRelationship;  // 보호자관계
}