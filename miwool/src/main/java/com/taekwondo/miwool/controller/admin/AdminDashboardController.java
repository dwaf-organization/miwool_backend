package com.taekwondo.miwool.controller.admin;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.admin.dashboard.respDto.DashboardRespDto;
import com.taekwondo.miwool.service.admin.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    /**
     * 관리자 대시보드 조회
     * GET /api/v1/admin/dashboard?month=2026-04
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(
            @RequestParam(value = "month") String month) {
        
        try {
            DashboardRespDto respDto = adminDashboardService.getDashboard(month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("대시보드 데이터를 조회했습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.warn("대시보드 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("대시보드 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("대시보드 조회 중 오류가 발생했습니다."));
        }
    }
}