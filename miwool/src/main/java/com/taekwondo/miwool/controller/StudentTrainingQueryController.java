package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.training.respDto.StudentTrainingDetailRespDto;
import com.taekwondo.miwool.dto.training.respDto.StudentTrainingInfoRespDto;
import com.taekwondo.miwool.service.StudentTrainingQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/students/training-info")
@RequiredArgsConstructor
public class StudentTrainingQueryController {
    
    private final StudentTrainingQueryService studentTrainingQueryService;
    
    /**
     * 학생별 수련정보 목록 조회
     * GET /api/v1/students/training-info?studentCode={studentCode}
     * 
     * @param studentCode Query Parameter
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @GetMapping
    public ResponseEntity<?> getStudentTrainingList(
            @RequestParam("studentCode") String studentCode,
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            List<StudentTrainingInfoRespDto> trainingList = 
                    studentTrainingQueryService.getStudentTrainingList(studentCode, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("수련정보 목록을 조회했습니다.", trainingList));
            
        } catch (Exception e) {
            log.error("수련정보 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("수련정보 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 수련정보 상세 조회
     * GET /api/v1/students/training-info/detail?studentCode={studentCode}&trainingInfoCode={trainingInfoCode}
     * 
     * @param studentCode Query Parameter
     * @param trainingInfoCode Query Parameter
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @GetMapping("/detail")
    public ResponseEntity<?> getStudentTrainingDetail(
            @RequestParam("studentCode") String studentCode,
            @RequestParam("trainingInfoCode") Integer trainingInfoCode,
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            StudentTrainingDetailRespDto detail = 
                    studentTrainingQueryService.getStudentTrainingDetail(
                            studentCode, trainingInfoCode, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("수련정보 상세를 조회했습니다.", detail));
            
        } catch (IllegalArgumentException e) {
            log.error("수련정보 상세 조회 중 오류: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("수련정보 상세 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("수련정보 상세 조회 중 오류가 발생했습니다."));
        }
    }
}