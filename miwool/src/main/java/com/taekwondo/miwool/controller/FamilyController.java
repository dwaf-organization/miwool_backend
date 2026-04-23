package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.family.reqDto.SaveFamilyInfoReqDto;
import com.taekwondo.miwool.dto.family.respDto.FamilyInfoRespDto;
import com.taekwondo.miwool.service.FamilyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/family")
@RequiredArgsConstructor
public class FamilyController {
    
    private final FamilyService familyService;
    
    /**
     * 가족 정보 조회
     * GET /api/v1/family/info?studentCode={studentCode}
     */
    @GetMapping("/info")
    public ResponseEntity<?> getFamilyInfo(
            @RequestParam("studentCode") String studentCode) {
        
        try {
            FamilyInfoRespDto respDto = familyService.getFamilyInfo(studentCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("가족 정보를 조회했습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.error("가족 정보 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("가족 정보 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("가족 정보 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 가족 정보 저장 (생성/수정)
     * POST /api/v1/family/info
     */
    @PostMapping("/info")
    public ResponseEntity<?> saveFamilyInfo(
            @Valid @RequestBody SaveFamilyInfoReqDto reqDto,
            BindingResult bindingResult) {
        
        // Validation 체크
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            familyService.saveFamilyInfo(reqDto);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("가족 정보를 저장했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("가족 정보 저장 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("가족 정보 저장 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("가족 정보 저장 중 오류가 발생했습니다."));
        }
    }
}