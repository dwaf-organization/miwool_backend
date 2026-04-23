package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.common.respDto.CommonCodeRespDto;
import com.taekwondo.miwool.dto.common.respDto.StudentSelectionRespDto;
import com.taekwondo.miwool.dto.common.respDto.TraitOptionDto;
import com.taekwondo.miwool.service.CommonCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/common")
@RequiredArgsConstructor
public class CommonCodeController {
    
    private final CommonCodeService commonCodeService;
    
    /**
     * 그룹코드별 공통코드 조회
     * GET /api/v1/common/codes?groupCode=PURPOSE
     */
    @GetMapping("/codes")
    public ResponseEntity<?> getCodesByGroupCode(@RequestParam("groupCode") String groupCode) {
        
        try {
            List<CommonCodeRespDto> codes = commonCodeService.getCodesByGroupCode(groupCode);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("공통코드 조회 성공", codes));
            
        } catch (Exception e) {
            log.error("공통코드 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("공통코드 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 특성 항목 조회
     * GET /api/v1/students/character-trait-options
     */
    @GetMapping("/character-trait-options")
    public ResponseEntity<?> getCharacterTraitOptions() {
        
        try {
            Map<String, List<TraitOptionDto>> options = commonCodeService.getCharacterTraitOptions();
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("특성 항목을 조회했습니다.", options));
            
        } catch (Exception e) {
            log.error("특성 항목 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("특성 항목 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 제자 선택 팝업 조회
     * GET /api/v1/common/students/selection
     */
    @GetMapping("/students/selection")
    public ResponseEntity<?> getStudentSelection(
            @RequestParam(value = "studentSearch", required = false) String studentSearch,
            @RequestParam(value = "beltCode", required = false) String beltCode,
            @RequestParam(value = "genderCode", required = false) String genderCode,
            @RequestParam(value = "grade", required = false) String grade,
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            // '전체' 문자열은 null로 변환
            String processedBeltCode = "전체".equals(beltCode) ? null : beltCode;
            String processedGrade = "전체".equals(grade) ? null : grade;
            
            // genderCode: "전체" → null, 숫자 문자열 → Integer 변환
            Integer processedGenderCode = null;
            if (genderCode != null && !"전체".equals(genderCode)) {
                try {
                    processedGenderCode = Integer.parseInt(genderCode);
                } catch (NumberFormatException e) {
                    log.warn("잘못된 genderCode 형식: {}", genderCode);
                }
            }
            
            List<StudentSelectionRespDto> result = commonCodeService.getStudentSelection(
                    studentSearch,
                    processedBeltCode,
                    processedGenderCode,
                    processedGrade,
                    dojangCode
            );
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("제자 목록을 조회했습니다.", result));
            
        } catch (Exception e) {
            log.error("제자 선택 팝업 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("제자 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
}