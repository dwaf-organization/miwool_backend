package com.taekwondo.miwool.dto.app.dashboard.respDto;

import com.taekwondo.miwool.dto.dashboard.respDto.CalendarRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.DailyRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.SummaryTabRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.WeeklyRespDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardRespDto {
    
    private String month; // 조회 월 (YYYYMM)
    private CalendarRespDto calendar; // 달력 탭
    private DailyRespDto daily; // 일일 탭
    private WeeklyRespDto weekly; // 주간 탭
    private SummaryTabRespDto summaryTab; // 요약 탭
}