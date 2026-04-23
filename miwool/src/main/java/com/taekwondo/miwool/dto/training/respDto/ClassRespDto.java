package com.taekwondo.miwool.dto.training.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassRespDto {
    
    private String classCode;            // 수업코드
    private String className;            // 수업명
    private String dayOfWeek;            // 요일 (월요일, 화요일, ...)
    private String classTime;            // 수업시간 ("11:00 ~ 13:00")
    private String description;          // 수업설명
    private String useYn;                // 사용여부
    private LocalDateTime createdAt;     // 생성일
    private LocalDateTime updatedAt;     // 수정일
}