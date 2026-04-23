package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.management.reqDto.BatchSaveEducationManagementReqDto;
import com.taekwondo.miwool.dto.management.reqDto.SaveEducationDetailReqDto;
import com.taekwondo.miwool.dto.management.respDto.EducationDetailRespDto;
import com.taekwondo.miwool.dto.management.respDto.EducationManagementItemDto;
import com.taekwondo.miwool.service.EducationManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/education-management")
@RequiredArgsConstructor
public class EducationManagementController {
    
    private final EducationManagementService educationManagementService;
    
    /**
     * 교육지도 목록 조회
     * GET /api/v1/education-management/list
     */
    @GetMapping("/list")
    public ResponseEntity<?> getEducationManagementList(
            @RequestParam("yearMonth") String yearMonth,
            @RequestParam(value = "studentSearch", required = false) String studentSearch,
            @RequestParam(value = "beltCode", required = false) String beltCode,
            @RequestParam(value = "genderCode", required = false) String genderCode,
            @RequestParam(value = "grade", required = false) String grade,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "category", required = false) String category,
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            // '전체' 문자열은 null로 변환
            String processedBeltCode = "전체".equals(beltCode) ? null : beltCode;
            String processedGrade = "전체".equals(grade) ? null : grade;
            String processedStatus = "전체".equals(status) ? null : status;
            String processedCategory = "전체".equals(category) ? null : category;
            
            // genderCode: "전체" → null, 숫자 문자열 → Integer 변환
            Integer processedGenderCode = null;
            if (genderCode != null && !"전체".equals(genderCode)) {
                try {
                    processedGenderCode = Integer.parseInt(genderCode);
                } catch (NumberFormatException e) {
                    log.warn("잘못된 genderCode 형식: {}", genderCode);
                }
            }
            
            List<EducationManagementItemDto> items = educationManagementService.getEducationManagementList(
                    yearMonth,
                    studentSearch,
                    processedBeltCode,
                    processedGenderCode,
                    processedGrade,
                    processedStatus,
                    processedCategory,
                    dojangCode
            );
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("교육지도 목록을 조회했습니다.", items));
            
        } catch (Exception e) {
            log.error("교육지도 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("교육지도 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 교육지도 일괄 저장
     * POST /api/v1/education-management/batch-save
     */
    @PostMapping("/batch-save")
    public ResponseEntity<?> batchSaveEducationManagement(
            @Valid @RequestBody BatchSaveEducationManagementReqDto reqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal String dojangCode) {
        
        // Validation 체크
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            educationManagementService.batchSaveEducationManagement(reqDto, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("교육지도를 저장했습니다.", null));
            
        } catch (Exception e) {
            log.error("교육지도 일괄 저장 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("교육지도 저장 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 교육지도 상세 조회
     * GET /api/v1/education-management/detail
     */
    @GetMapping("/detail")
    public ResponseEntity<?> getEducationDetail(
            @RequestParam("studentCode") String studentCode,
            @RequestParam("yearMonth") String yearMonth) {
        
        try {
            EducationDetailRespDto detail = educationManagementService.getEducationDetail(
                    studentCode, 
                    yearMonth
            );
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("교육지도 상세를 조회했습니다.", detail));
            
        } catch (IllegalArgumentException e) {
            log.error("교육지도 상세 조회 중 오류: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("교육지도 상세 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("교육지도 상세 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 교육지도 상세 저장
     * POST /api/v1/education-management/save-detail
     */
    @PostMapping("/save-detail")
    public ResponseEntity<?> saveEducationDetail(
            @Valid @RequestBody SaveEducationDetailReqDto reqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal String dojangCode) {
        
        // Validation 체크
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            educationManagementService.saveEducationDetail(reqDto, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("교육지도를 저장했습니다.", null));
            
        } catch (Exception e) {
            log.error("교육지도 상세 저장 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("교육지도 저장 중 오류가 발생했습니다."));
        }
    }
}