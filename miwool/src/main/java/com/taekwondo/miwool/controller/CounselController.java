package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.counsel.reqDto.CreateCounselReqDto;
import com.taekwondo.miwool.dto.counsel.reqDto.UpdateCounselReqDto;
import com.taekwondo.miwool.dto.counsel.respDto.CounselDetailRespDto;
import com.taekwondo.miwool.dto.counsel.respDto.CounselListRespDto;
import com.taekwondo.miwool.service.CounselService;
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
@RequestMapping("/api/v1/counsel")
@RequiredArgsConstructor
public class CounselController {
    
    private final CounselService counselService;
    
    /**
     * 상담 목록 조회
     * GET /api/v1/counsel/list?studentCode={studentCode}&page=1&size=10
     */
    @GetMapping("/list")
    public ResponseEntity<?> getCounselList(
            @RequestParam("studentCode") String studentCode,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        try {
            CounselListRespDto respDto = counselService.getCounselList(studentCode, page, size);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("상담 목록을 조회했습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.error("상담 목록 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("상담 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("상담 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 상담 상세 조회
     * GET /api/v1/counsel/detail?counselCode={counselCode}
     */
    @GetMapping("/detail")
    public ResponseEntity<?> getCounselDetail(
            @RequestParam("counselCode") String counselCode) {
        
        try {
            CounselDetailRespDto respDto = counselService.getCounselDetail(counselCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("상담 상세 정보를 조회했습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.error("상담 상세 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("상담 상세 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("상담 상세 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 상담 생성
     * POST /api/v1/counsel/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> createCounsel(
            @Valid @RequestBody CreateCounselReqDto reqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal String dojangCode) {
        
        // Validation 체크
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            counselService.createCounsel(reqDto, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("상담을 생성했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("상담 생성 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("상담 생성 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("상담 생성 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 상담 수정
     * POST /api/v1/counsel/update
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateCounsel(
            @Valid @RequestBody UpdateCounselReqDto reqDto,
            BindingResult bindingResult) {
        
        // Validation 체크
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            counselService.updateCounsel(reqDto);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("상담을 수정했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("상담 수정 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("상담 수정 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("상담 수정 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 상담 삭제
     * DELETE /api/v1/counsel?counselCode={counselCode}&studentCode={studentCode}
     */
    @DeleteMapping
    public ResponseEntity<?> deleteCounsel(
            @RequestParam("counselCode") String counselCode,
            @RequestParam("studentCode") String studentCode) {
        
        try {
            counselService.deleteCounsel(counselCode, studentCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("상담을 삭제했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("상담 삭제 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("상담 삭제 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("상담 삭제 중 오류가 발생했습니다."));
        }
    }
}