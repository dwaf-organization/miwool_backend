package com.taekwondo.miwool.dto.training.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassStudentDto {
    
    private String studentCode;          // 제자코드
    private int genderCode;              // 성별코드
    private String studentName;          // 제자명
    private int age;                     // 나이 (한국나이)
    private String grade;                // 학년
    private String beltCode;             // 급수코드
    private String beltName;             // 급수명 (노란띠, 파란띠, ...)
    private String ropeBeltCode;     // 줄넘기 급수코드
    private String ropeBeltName;     // 줄넘기 급수명
    private int useVehicle;              // 차량이용여부 (1=이용, 0=미이용)
    private String pickupLocation;       // 탑승장소
    private String dropoffLocation;      // 하차장소
    private String handoverMethod;       // 보호자인계방식
}