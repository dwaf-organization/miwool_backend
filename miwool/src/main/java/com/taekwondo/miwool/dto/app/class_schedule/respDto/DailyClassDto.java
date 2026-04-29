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
public class DailyClassDto {
    
    private String date; // 날짜(요일) - 예: "2026-04-27(월요일)"
    private List<TodayClassItemDto> classes; // 해당 날짜의 수업 목록
}