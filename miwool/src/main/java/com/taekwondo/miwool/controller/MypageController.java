package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.mypage.reqDto.UpdateDojangInfoReqDto;
import com.taekwondo.miwool.dto.mypage.respDto.DojangInfoRespDto;
import com.taekwondo.miwool.service.MypageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/mypage")
@RequiredArgsConstructor
public class MypageController {
    
    private final MypageService mypageService;
    
    /**
     * 도장 정보 조회
     * GET /api/v1/mypage/dojang-info
     */
    @GetMapping("/dojang-info")
    public ResponseEntity<?> getDojangInfo(
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            DojangInfoRespDto dojangInfo = mypageService.getDojangInfo(dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("도장 정보를 조회했습니다.", dojangInfo));
            
        } catch (IllegalArgumentException e) {
            log.error("도장 정보 조회 중 오류: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("도장 정보 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("도장 정보 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 도장 정보 수정
     * PUT /api/v1/mypage/dojang-info
     */
    @PutMapping("/dojang-info")
    public ResponseEntity<?> updateDojangInfo(
            @Valid @RequestBody UpdateDojangInfoReqDto reqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal String dojangCode) {
        
        // Validation 체크
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            mypageService.updateDojangInfo(dojangCode, reqDto);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("도장 정보를 수정했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("도장 정보 수정 중 오류: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("도장 정보 수정 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("도장 정보 수정 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 도장 탈퇴
     * DELETE /api/v1/mypage/dojang-withdraw
     */
    @DeleteMapping("/dojang-withdraw")
    public ResponseEntity<?> withdrawDojang(
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            mypageService.withdrawDojang(dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("도장 탈퇴가 완료되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("도장 탈퇴 중 오류: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("도장 탈퇴 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("도장 탈퇴 중 오류가 발생했습니다."));
        }
    }
}