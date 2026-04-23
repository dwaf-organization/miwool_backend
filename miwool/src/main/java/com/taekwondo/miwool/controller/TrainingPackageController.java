package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.training.reqDto.CreateTrainingPackageReqDto;
import com.taekwondo.miwool.dto.training.reqDto.UpdateTrainingPackageReqDto;
import com.taekwondo.miwool.dto.training.respDto.PackageOptionRespDto;
import com.taekwondo.miwool.dto.training.respDto.TrainingPackageRespDto;
import com.taekwondo.miwool.service.TrainingPackageService;
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
@RequestMapping("/api/v1/training/package")
@RequiredArgsConstructor
public class TrainingPackageController {
    
    private final TrainingPackageService trainingPackageService;
    
    /**
     * 패키지 생성
     * POST /api/v1/training/package
     * 
     * @param reqDto Request Body
     * @param bindingResult Validation 결과
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @PostMapping
    public ResponseEntity<?> createPackage(
            @Valid @RequestBody CreateTrainingPackageReqDto reqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal String dojangCode) {
        
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            trainingPackageService.createPackage(reqDto, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("패키지를 생성했습니다.", null));
            
        } catch (Exception e) {
            log.error("패키지 생성 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("패키지 생성 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 패키지 목록 조회
     * GET /api/v1/training/package
     * 
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @GetMapping
    public ResponseEntity<?> getPackageList(
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            List<TrainingPackageRespDto> packages = trainingPackageService.getPackageList(dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("패키지 목록을 조회했습니다.", packages));
            
        } catch (Exception e) {
            log.error("패키지 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("패키지 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 패키지 선택용 목록 조회
     * GET /api/v1/training/package/options
     * 
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @GetMapping("/options")
    public ResponseEntity<?> getPackageOptions(
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            List<PackageOptionRespDto> options = trainingPackageService.getPackageOptions(dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("패키지 선택 목록을 조회했습니다.", options));
            
        } catch (Exception e) {
            log.error("패키지 선택 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("패키지 선택 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 패키지 상세 조회
     * GET /api/v1/training/package/detail?packageCode=MW26001-PKG001
     * 
     * @param packageCode Query Parameter - 패키지코드
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @GetMapping("/detail")
    public ResponseEntity<?> getPackageDetail(
            @RequestParam("packageCode") String packageCode,
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            TrainingPackageRespDto packageDetail = trainingPackageService.getPackageDetail(packageCode, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("패키지 상세를 조회했습니다.", packageDetail));
            
        } catch (IllegalArgumentException e) {
            log.error("패키지 상세 조회 중 오류: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("패키지 상세 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("패키지 상세 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 패키지 수정
     * PUT /api/v1/training/package/update
     * 
     * @param reqDto Request Body (packageCode 포함)
     * @param bindingResult Validation 결과
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @PutMapping("/update")
    public ResponseEntity<?> updatePackage(
            @Valid @RequestBody UpdateTrainingPackageReqDto reqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal String dojangCode) {
        
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            trainingPackageService.updatePackage(reqDto, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("패키지를 수정했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("패키지 수정 중 오류: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("패키지 수정 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("패키지 수정 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 패키지 삭제
     * DELETE /api/v1/training/package?packageCode=MW26001-PKG001
     * 
     * @param packageCode Query Parameter - 패키지코드
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @DeleteMapping
    public ResponseEntity<?> deletePackage(
            @RequestParam("packageCode") String packageCode,
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            trainingPackageService.deletePackage(packageCode, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("패키지를 삭제했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("패키지 삭제 중 오류: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("패키지 삭제 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("패키지 삭제 중 오류가 발생했습니다."));
        }
    }
}