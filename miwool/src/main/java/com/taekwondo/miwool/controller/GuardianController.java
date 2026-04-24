package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.guardian.reqDto.SaveGuardianReqDto;
import com.taekwondo.miwool.dto.guardian.respDto.GuardianDetailRespDto;
import com.taekwondo.miwool.dto.guardian.respDto.GuardianInfoRespDto;
import com.taekwondo.miwool.service.GuardianService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/guardians")
@RequiredArgsConstructor
public class GuardianController {
    
    private final GuardianService guardianService;
    
    /**
     * 보호자 정보 조회
     * GET /api/v1/guardians/info?studentCode={studentCode}
     */
    @GetMapping("/info")
    public ResponseEntity<?> getGuardianInfo(
            @RequestParam("studentCode") String studentCode) {
        
        try {
            GuardianInfoRespDto respDto = guardianService.getGuardianInfo(studentCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("보호자 정보를 조회했습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.error("보호자 정보 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("보호자 정보 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("보호자 정보 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 보호자 상세 조회
     * GET /api/v1/guardians/detail?guardianCode={guardianCode}&studentCode={studentCode}
     */
    @GetMapping("/detail")
    public ResponseEntity<?> getGuardianDetail(
            @RequestParam(value = "guardianCode", required = true) String guardianCode,
            @RequestParam(value = "studentCode", required = true) String studentCode) {
        
        try {
            GuardianDetailRespDto respDto = guardianService.getGuardianDetail(guardianCode, studentCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("보호자 상세 정보를 조회했습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.error("보호자 상세 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("보호자 상세 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("보호자 상세 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 보호자 저장 (생성/수정)
     * POST /api/v1/guardians
     * guardianCode가 null이면 생성, 있으면 수정
     */
    @PostMapping
    public ResponseEntity<?> saveGuardian(
            @Valid @RequestBody SaveGuardianReqDto reqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal String dojangCode) {
        
        // Validation 체크
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            guardianService.saveGuardian(reqDto, dojangCode);
            
            String message = (reqDto.getGuardianCode() == null || reqDto.getGuardianCode().isEmpty())
                    ? "보호자를 생성했습니다."
                    : "보호자를 수정했습니다.";
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success(message, null));
            
        } catch (IllegalArgumentException e) {
            log.error("보호자 저장 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("보호자 저장 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("보호자 저장 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 보호자 삭제
     * DELETE /api/v1/guardians?studentCode={studentCode}&guardianCode={guardianCode}
     */
    @DeleteMapping
    public ResponseEntity<?> deleteGuardian(
            @RequestParam("studentCode") String studentCode,
            @RequestParam("guardianCode") String guardianCode) {
        
        try {
            guardianService.deleteGuardian(studentCode, guardianCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("보호자를 삭제했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("보호자 삭제 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("보호자 삭제 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("보호자 삭제 중 오류가 발생했습니다."));
        }
    }
}