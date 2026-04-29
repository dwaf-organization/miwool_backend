package com.taekwondo.miwool.dto.app.class_schedule.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayClassItemDto {
    
    private String classCode; // 수업코드
    private String classTime; // 수업시간 (10:00~12:00)
    private String className; // 수업명
    private Integer participantCount; // 참여인원수
}