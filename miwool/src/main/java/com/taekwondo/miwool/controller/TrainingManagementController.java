package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.training.reqDto.UpdateTrainingReqDto;
import com.taekwondo.miwool.service.TrainingManagementService;
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
@RequestMapping("/api/v1/training/student")
@RequiredArgsConstructor
public class TrainingManagementController {
    
    private final TrainingManagementService trainingManagementService;
    
    /**
     * 수련 수정
     * PUT /api/v1/training/student/update
     * 
     * @param reqDto Request Body
     * @param bindingResult Validation 결과
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateTraining(
            @Valid @RequestBody UpdateTrainingReqDto reqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal String dojangCode) {
        
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            trainingManagementService.updateTraining(reqDto, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("수련 정보가 수정되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("수련 수정 중 오류: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("수련 수정 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("수련 수정 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 수련 삭제
     * DELETE /api/v1/training/student?trainingInfoCode={trainingInfoCode}
     * 
     * @param trainingInfoCode Query Parameter
     * @param dojangCode 토큰에서 추출한 도장코드
     */
    @DeleteMapping
    public ResponseEntity<?> deleteTraining(
            @RequestParam("trainingInfoCode") Integer trainingInfoCode,
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            trainingManagementService.deleteTraining(trainingInfoCode, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("수련 정보가 삭제되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("수련 삭제 중 오류: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("수련 삭제 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("수련 삭제 중 오류가 발생했습니다."));
        }
    }
}