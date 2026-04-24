package com.taekwondo.miwool.dto.app.training.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassOptionRespDto {
    
    private String classCode; // 수업코드
    private String displayText; // "월요일 오전반 11:00~13:00"
    private String dayOfWeek; // 요일 (월요일, 화요일, 수요일, 목요일, 금요일)
}