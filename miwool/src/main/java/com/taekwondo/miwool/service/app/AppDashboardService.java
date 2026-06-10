package com.taekwondo.miwool.service.app;

import com.taekwondo.miwool.dto.app.dashboard.respDto.DashboardRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.CalendarRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.DailyRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.PopupRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.SummaryTabRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.WeeklyRespDto;
import com.taekwondo.miwool.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppDashboardService {

    private final DashboardService dashboardService;

    /**
     * 앱 대시보드 전체 데이터 조회 (통합 API)
     */
    @Transactional(readOnly = true)
    public DashboardRespDto getAllDashboardData(String dojangCode, String month) {
        log.info("앱 대시보드 전체 데이터 조회: dojangCode={}, month={}", dojangCode, month);
        
        CalendarRespDto calendar = getCalendarData(dojangCode, month);
        DailyRespDto daily = getDailyPaymentData(dojangCode, month);
        WeeklyRespDto weekly = getWeeklyData(dojangCode, month);
        SummaryTabRespDto summaryTab = getSummaryData(dojangCode, month);
        
        return DashboardRespDto.builder()
                .month(month)
                .calendar(calendar)
                .daily(daily)
                .weekly(weekly)
                .summaryTab(summaryTab)
                .build();
    }

    /**
     * 앱 달력 탭 데이터 조회
     */
    @Transactional(readOnly = true)
    public CalendarRespDto getCalendarData(String dojangCode, String month) {
        log.info("앱 달력 데이터 조회: dojangCode={}, month={}", dojangCode, month);
        return dashboardService.getCalendarData(dojangCode, month);
    }

    /**
     * 앱 일일 탭 데이터 조회
     */
    @Transactional(readOnly = true)
    public DailyRespDto getDailyPaymentData(String dojangCode, String month) {
        log.info("앱 일일 납부 현황 조회: dojangCode={}, month={}", dojangCode, month);
        return dashboardService.getDailyPaymentData(dojangCode, month);
    }

    /**
     * 앱 주간 탭 데이터 조회
     */
    @Transactional(readOnly = true)
    public WeeklyRespDto getWeeklyData(String dojangCode, String month) {
        log.info("앱 주간 데이터 조회: dojangCode={}, month={}", dojangCode, month);
        return dashboardService.getWeeklyData(dojangCode, month);
    }

    /**
     * 앱 요약 탭 데이터 조회
     */
    @Transactional(readOnly = true)
    public SummaryTabRespDto getSummaryData(String dojangCode, String month) {
        log.info("앱 요약 데이터 조회: dojangCode={}, month={}", dojangCode, month);
        return dashboardService.getSummaryData(dojangCode, month);
    }
    
    /**
     * 앱 달력 팝업 데이터 조회
     */
    @Transactional(readOnly = true)
    public PopupRespDto getPopupData(String dojangCode, String date) {
        log.info("앱 달력 팝업 데이터 조회: dojangCode={}, date={}", dojangCode, date);
        return dashboardService.getPopupData(dojangCode, date);
    }
    
}