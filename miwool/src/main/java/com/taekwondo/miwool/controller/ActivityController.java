package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.activity.reqDto.CreateActivityReqDto;
import com.taekwondo.miwool.dto.activity.reqDto.UpdateActivityReqDto;
import com.taekwondo.miwool.dto.activity.respDto.ActivityDetailRespDto;
import com.taekwondo.miwool.dto.activity.respDto.ActivityListRespDto;
import com.taekwondo.miwool.dto.activity.respDto.StudentActivityDetailRespDto;
import com.taekwondo.miwool.dto.activity.respDto.StudentActivityListRespDto;
import com.taekwondo.miwool.service.ActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class ActivityController {
    
    private final ActivityService activityService;
    
    /**
     * 활동내역 조회
     * GET /api/v1/activities
     */
    @GetMapping
    public ResponseEntity<?> getActivityList(
            @RequestParam(value = "activityCodeOrName", required = false) String activityCodeOrName,
            @RequestParam(value = "activityType", required = false) String activityType,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        try {
            // '전체' 문자열은 null로 변환
            String processedActivityType = "전체".equals(activityType) ? null : activityType;
            
            ActivityListRespDto respDto = activityService.getActivityList(
                    activityCodeOrName,
                    processedActivityType,
                    startDate,
                    endDate
            );
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("활동 목록을 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("활동 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("활동 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 활동 상세 조회
     * GET /api/v1/activities/detail
     */
    @GetMapping("/detail")
    public ResponseEntity<?> getActivityDetail(
            @RequestParam("activityCode") String activityCode) {
        
        try {
            ActivityDetailRespDto respDto = activityService.getActivityDetail(activityCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("활동 상세 정보를 조회했습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.error("활동 상세 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("활동 상세 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("활동 상세 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 활동 생성
     * POST /api/v1/activities/create
     */
    @PostMapping("/create")
    public ResponseEntity<?> createActivity(
            @Valid @RequestBody CreateActivityReqDto reqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal String dojangCode) {
        
        // Validation 체크
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            activityService.createActivity(reqDto, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("활동을 생성했습니다.", null));
            
        } catch (Exception e) {
            log.error("활동 생성 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("활동 생성 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 활동 수정
     * POST /api/v1/activities/update
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateActivity(
            @Valid @RequestBody UpdateActivityReqDto reqDto,
            BindingResult bindingResult) {
        
        // Validation 체크
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            activityService.updateActivity(reqDto);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("활동을 수정했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("활동 수정 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("활동 수정 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("활동 수정 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 활동 삭제
     * DELETE /api/v1/activities
     */
    @DeleteMapping
    public ResponseEntity<?> deleteActivity(
            @RequestParam("activityCode") String activityCode) {
        
        try {
            activityService.deleteActivity(activityCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("활동을 삭제했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("활동 삭제 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("활동 삭제 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("활동 삭제 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 제자 활동 목록 조회
     * GET /api/v1/activities/student-activity-list
     */
    @GetMapping("/student-activity-list")
    public ResponseEntity<?> getStudentActivityList(
            @RequestParam("studentCode") String studentCode,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        
        try {
            StudentActivityListRespDto respDto = activityService.getStudentActivityList(studentCode, page, size);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("제자 활동 목록을 조회했습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.error("제자 활동 목록 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("제자 활동 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("제자 활동 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 제자 활동 상세 조회
     * GET /api/v1/activities/student-activity-detail
     */
    @GetMapping("/student-activity-detail")
    public ResponseEntity<?> getStudentActivityDetail(
            @RequestParam("studentCode") String studentCode,
            @RequestParam("activityCode") String activityCode) {
        
        try {
            StudentActivityDetailRespDto respDto = activityService.getStudentActivityDetail(studentCode, activityCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("제자 활동 상세 정보를 조회했습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.error("제자 활동 상세 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("제자 활동 상세 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("제자 활동 상세 조회 중 오류가 발생했습니다."));
        }
    }
}