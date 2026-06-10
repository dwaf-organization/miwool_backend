package com.taekwondo.miwool.controller.app;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.app.dashboard.respDto.DashboardRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.CalendarRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.DailyRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.PopupRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.SummaryTabRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.WeeklyRespDto;
import com.taekwondo.miwool.service.app.AppDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/app/dashboard")
@RequiredArgsConstructor
public class AppDashboardController {

    private final AppDashboardService appDashboardService;

    // 앱 대시보드 전체 데이터 조회 (통합 API)
    @GetMapping("/all")
    public ResponseEntity<?> getAllDashboardData(
            @RequestParam(value = "dojangCode", required = true) String dojangCode,
            @RequestParam(value = "month", required = true) String month) {
        
        try {
            DashboardRespDto respDto = appDashboardService.getAllDashboardData(dojangCode, month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("대시보드 전체 데이터를 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("앱 대시보드 전체 데이터 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("대시보드 전체 데이터 조회 중 오류가 발생했습니다."));
        }
    }

    // 앱 달력 탭 데이터 조회
    @GetMapping("/calendar")
    public ResponseEntity<?> getCalendarData(
            @RequestParam(value = "dojangCode", required = true) String dojangCode,
            @RequestParam(value = "month", required = true) String month) {
        
        try {
            CalendarRespDto respDto = appDashboardService.getCalendarData(dojangCode, month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("달력 데이터를 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("앱 달력 데이터 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("달력 데이터 조회 중 오류가 발생했습니다."));
        }
    }

    // 앱 일일 탭 데이터 조회
    @GetMapping("/daily")
    public ResponseEntity<?> getDailyPaymentData(
            @RequestParam(value = "dojangCode", required = true) String dojangCode,
            @RequestParam(value = "month", required = true) String month) {
        
        try {
            DailyRespDto respDto = appDashboardService.getDailyPaymentData(dojangCode, month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("일일 납부 현황을 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("앱 일일 납부 현황 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("일일 납부 현황 조회 중 오류가 발생했습니다."));
        }
    }

    // 앱 주간 탭 데이터 조회
    @GetMapping("/weekly")
    public ResponseEntity<?> getWeeklyData(
            @RequestParam(value = "dojangCode", required = true) String dojangCode,
            @RequestParam(value = "month", required = true) String month) {
        
        try {
            WeeklyRespDto respDto = appDashboardService.getWeeklyData(dojangCode, month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("주간 데이터를 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("앱 주간 데이터 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("주간 데이터 조회 중 오류가 발생했습니다."));
        }
    }

    // 앱 요약 탭 데이터 조회
    @GetMapping("/summary")
    public ResponseEntity<?> getSummaryData(
            @RequestParam(value = "dojangCode", required = true) String dojangCode,
            @RequestParam(value = "month", required = true) String month) {
        
        try {
            SummaryTabRespDto respDto = appDashboardService.getSummaryData(dojangCode, month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("요약 데이터를 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("앱 요약 데이터 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("요약 데이터 조회 중 오류가 발생했습니다."));
        }
    }
    
    // 앱 달력 팝업 데이터 조회
    @GetMapping("/calendar/popup")
    public ResponseEntity<?> getCalendarPopup(
            @RequestParam(value = "dojangCode", required = true) String dojangCode,
            @RequestParam(value = "date", required = true) String date) {
        
        try {
            PopupRespDto respDto = appDashboardService.getPopupData(dojangCode, date);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("팝업 데이터를 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("앱 달력 팝업 데이터 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("팝업 데이터 조회 중 오류가 발생했습니다."));
        }
    }
    
}