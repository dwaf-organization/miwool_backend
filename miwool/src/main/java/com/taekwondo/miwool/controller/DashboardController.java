package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.CalendarRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.DailyRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.SummaryTabRespDto;
import com.taekwondo.miwool.dto.dashboard.respDto.WeeklyRespDto;
import com.taekwondo.miwool.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // 달력 데이터 조회
    @GetMapping("/calendar")
    public ResponseEntity<?> getCalendarData(
    		@AuthenticationPrincipal String dojangCode,
            @RequestParam(value = "month", required = true) String month) {
        
        try {            
            CalendarRespDto respDto = dashboardService.getCalendarData(dojangCode, month);
            
            return ResponseEntity 
                    .ok()
                    .body(RespDto.success("달력 데이터를 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("달력 데이터 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("달력 데이터 조회 중 오류가 발생했습니다."));
        }
    }
    
    // 일일 납부 현황 조회
    @GetMapping("/daily")
    public ResponseEntity<?> getDailyPaymentData(
            @AuthenticationPrincipal String dojangCode,
            @RequestParam(value = "month", required = true) String month) {
        
        try {
            DailyRespDto respDto = dashboardService.getDailyPaymentData(dojangCode, month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("일일 납부 현황을 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("일일 납부 현황 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("일일 납부 현황 조회 중 오류가 발생했습니다."));
        }
    }
    
    // 주간 데이터 조회
    @GetMapping("/weekly")
    public ResponseEntity<?> getWeeklyData(
            @AuthenticationPrincipal String dojangCode,
            @RequestParam(value = "month", required = true) String month) {
        
        try {
            WeeklyRespDto respDto = dashboardService.getWeeklyData(dojangCode, month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("주간 데이터를 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("주간 데이터 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("주간 데이터 조회 중 오류가 발생했습니다."));
        }
    }
    
    // 요약 데이터 조회
    @GetMapping("/summary")
    public ResponseEntity<?> getSummaryData(
            @AuthenticationPrincipal String dojangCode,
            @RequestParam(value = "month", required = true) String month) {
        
        try {
            SummaryTabRespDto respDto = dashboardService.getSummaryData(dojangCode, month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("요약 데이터를 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("요약 데이터 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("요약 데이터 조회 중 오류가 발생했습니다."));
        }
    }
    
}