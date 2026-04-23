package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.statistics.respDto.StatisticsDashboardRespDto;
import com.taekwondo.miwool.dto.statistics.respDto.StudentManagementSummaryRespDto;
import com.taekwondo.miwool.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    // 통계 대시보드 조회
    @GetMapping("/dashboard")
    public ResponseEntity<?> getStatisticsDashboard(
            @AuthenticationPrincipal String dojangCode,
            @RequestParam(value = "month", required = true) String month) {
        
        try {
            StatisticsDashboardRespDto respDto = statisticsService.getStatisticsDashboard(dojangCode, month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("통계 대시보드를 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("통계 대시보드 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("통계 대시보드 조회 중 오류가 발생했습니다."));
        }
    }

    // 제자관리결산 조회
    @GetMapping("/student-management-summary")
    public ResponseEntity<?> getStudentManagementSummary(
            @AuthenticationPrincipal String dojangCode,
            @RequestParam(value = "month", required = true) String month) {
        
        try {
            StudentManagementSummaryRespDto respDto = statisticsService.getStudentManagementSummary(dojangCode, month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("제자관리결산을 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("제자관리결산 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("제자관리결산 조회 중 오류가 발생했습니다."));
        }
    }
    
}