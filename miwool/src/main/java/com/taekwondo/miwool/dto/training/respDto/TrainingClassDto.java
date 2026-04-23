package com.taekwondo.miwool.dto.training.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingClassDto {
    
    private String classCode;        // 수업코드
    private String className;        // 수업명
    private String dayOfWeek;        // 요일
    private String classTime;        // 수업시간 (11:00~13:00)
}