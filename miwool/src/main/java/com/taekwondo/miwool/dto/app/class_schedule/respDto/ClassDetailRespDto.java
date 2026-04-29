package com.taekwondo.miwool.dto.app.class_schedule.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassDetailRespDto {
    
    private String className; // 수업명
    private String dayOfWeek; // 요일
    private String classTime; // 시간 (10:00~12:00)
    private List<ClassStudentDetailDto> students; // 제자 목록
}