package com.taekwondo.miwool.dto.training.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassOptionRespDto {
    
    private String classCode;            // 수업코드
    private String displayText;          // "월요일 오전반 11:00 ~ 13:00"
}