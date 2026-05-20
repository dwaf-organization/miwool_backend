package com.taekwondo.miwool.controller.admin;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedConsultationRespDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedEducationRespDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedTuitionRespDto;
import com.taekwondo.miwool.service.admin.AdminIntegratedConsultationService;
import com.taekwondo.miwool.service.admin.AdminIntegratedEducationService;
import com.taekwondo.miwool.service.admin.AdminIntegratedTuitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/integrated")
@RequiredArgsConstructor
public class AdminIntegratedController {
    
    private final AdminIntegratedTuitionService adminIntegratedTuitionService;
    private final AdminIntegratedConsultationService adminIntegratedConsultationService;
    private final AdminIntegratedEducationService adminIntegratedEducationService;
    
    /**
     * 교육비 통합 데이터 조회
     * GET /api/v1/admin/integrated/tuition?month={month}
     */
    @GetMapping("/tuition")
    public ResponseEntity<?> getIntegratedTuition(
            @RequestParam(value = "month") String month) {
        
        try {
            log.info("교육비 통합 데이터 조회 요청: month={}", month);
            
            IntegratedTuitionRespDto data = adminIntegratedTuitionService.getIntegratedTuition(month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("교육비 통합 데이터를 조회했습니다.", data));
            
        } catch (Exception e) {
            log.error("교육비 통합 데이터 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("교육비 통합 데이터 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 상담관리 통합 데이터 조회
     * GET /api/v1/admin/integrated/consultation?month={month}
     */
    @GetMapping("/consultation")
    public ResponseEntity<?> getIntegratedConsultation(
            @RequestParam(value = "month") String month) {
        
        try {
            log.info("상담관리 통합 데이터 조회 요청: month={}", month);
            
            IntegratedConsultationRespDto data = adminIntegratedConsultationService.getIntegratedConsultation(month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("상담관리 통합 데이터를 조회했습니다.", data));
            
        } catch (Exception e) {
            log.error("상담관리 통합 데이터 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("상담관리 통합 데이터 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 교육지도관리 통합 데이터 조회
     * GET /api/v1/admin/integrated/education?month={month}
     */
    @GetMapping("/education")
    public ResponseEntity<?> getIntegratedEducation(
            @RequestParam(value = "month") String month) {
        
        try {
            log.info("교육지도관리 통합 데이터 조회 요청: month={}", month);
            
            IntegratedEducationRespDto data = adminIntegratedEducationService.getIntegratedEducation(month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("교육지도관리 통합 데이터를 조회했습니다.", data));
            
        } catch (Exception e) {
            log.error("교육지도관리 통합 데이터 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("교육지도관리 통합 데이터 조회 중 오류가 발생했습니다."));
        }
    }


}