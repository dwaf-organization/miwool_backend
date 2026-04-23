package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.training.reqDto.CreateClassReqDto;
import com.taekwondo.miwool.dto.training.reqDto.UpdateClassReqDto;
import com.taekwondo.miwool.dto.training.respDto.ClassDetailRespDto;
import com.taekwondo.miwool.dto.training.respDto.ClassOptionRespDto;
import com.taekwondo.miwool.dto.training.respDto.ClassRespDto;
import com.taekwondo.miwool.service.ClassService;
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
@RequestMapping("/api/v1/training/class")
@RequiredArgsConstructor
public class ClassController {
    
    private final ClassService classService;
    
    /**
     * 수업 생성
     * POST /api/v1/training/class
     * 
     * @param reqDto Request Body
     * @param bindingResult Validation 결과
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @PostMapping
    public ResponseEntity<?> createClass(
            @Valid @RequestBody CreateClassReqDto reqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal String dojangCode) {
        
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            classService.createClass(reqDto, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("수업을 생성했습니다.", null));
            
        } catch (Exception e) {
            log.error("수업 생성 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("수업 생성 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 수업 목록 조회
     * GET /api/v1/training/class
     * 
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @GetMapping
    public ResponseEntity<?> getClassList(
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            List<ClassRespDto> classes = classService.getClassList(dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("수업 목록을 조회했습니다.", classes));
            
        } catch (Exception e) {
            log.error("수업 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("수업 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 수업 선택용 목록 조회
     * GET /api/v1/training/class/options
     * 
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @GetMapping("/options")
    public ResponseEntity<?> getClassOptions(
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            List<ClassOptionRespDto> options = classService.getClassOptions(dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("수업 선택 목록을 조회했습니다.", options));
            
        } catch (Exception e) {
            log.error("수업 선택 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("수업 선택 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 수업 상세 조회 (학생 목록 포함)
     * GET /api/v1/training/class/detail?classCode=CLS001
     * 
     * @param classCode Query Parameter - 수업코드
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @GetMapping("/detail")
    public ResponseEntity<?> getClassDetail(
            @RequestParam("classCode") String classCode,
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            ClassDetailRespDto classDetail = classService.getClassDetail(classCode, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("수업 상세를 조회했습니다.", classDetail));
            
        } catch (IllegalArgumentException e) {
            log.error("수업 상세 조회 중 오류: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("수업 상세 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("수업 상세 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 수업 수정
     * PUT /api/v1/training/class/update
     * 
     * @param reqDto Request Body (classCode 포함)
     * @param bindingResult Validation 결과
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateClass(
            @Valid @RequestBody UpdateClassReqDto reqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal String dojangCode) {
        
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            classService.updateClass(reqDto, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("수업을 수정했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("수업 수정 중 오류: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("수업 수정 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("수업 수정 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 수업 삭제
     * DELETE /api/v1/training/class?classCode=CLS001
     * 
     * @param classCode Query Parameter - 수업코드
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @DeleteMapping
    public ResponseEntity<?> deleteClass(
            @RequestParam("classCode") String classCode,
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            classService.deleteClass(classCode, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("수업을 삭제했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("수업 삭제 중 오류: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("수업 삭제 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("수업 삭제 중 오류가 발생했습니다."));
        }
    }
}