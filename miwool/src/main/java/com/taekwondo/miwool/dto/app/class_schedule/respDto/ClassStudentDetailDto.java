package com.taekwondo.miwool.dto.app.class_schedule.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassStudentDetailDto {
    
    private Integer genderCode; // 성별코드
    private String studentName; // 제자명
    private Integer age; // 나이
    private String grade; // 학년
    private String beltName; // 급수명
    private String ropeBeltCode;     // 줄넘기 급수코드
    private String ropeBeltName;     // 줄넘기 급수명
    private String guardianPhone; // 보호자연락처
    private String studentPhone; // 제자연락처
    private Integer useVehicle; // 차량이용여부 (1=이용, 0=미이용)
    private String pickupLocation; // 승차위치
    private String dropoffLocation; // 하차위치
    private String handoverMethod; // 보호자인계방식
}