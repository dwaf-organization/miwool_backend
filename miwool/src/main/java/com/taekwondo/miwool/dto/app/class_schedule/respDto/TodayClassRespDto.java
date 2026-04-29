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
public class TodayClassRespDto {
    
    private List<DailyClassDto> days; // 4일치 수업 데이터
}