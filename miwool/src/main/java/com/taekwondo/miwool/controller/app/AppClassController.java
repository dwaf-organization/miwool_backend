package com.taekwondo.miwool.controller.app;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.app.class_schedule.respDto.ClassDetailRespDto;
import com.taekwondo.miwool.dto.app.class_schedule.respDto.TodayClassRespDto;
import com.taekwondo.miwool.service.app.AppClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/app/classes")
@RequiredArgsConstructor
public class AppClassController {

    private final AppClassService appClassService;

    // 앱 오늘의 수업 조회 (4일치)
    @GetMapping("/today")
    public ResponseEntity<?> getTodayClasses(
            @RequestParam(value = "dojangCode", required = true) String dojangCode) {
        
        try {
            TodayClassRespDto respDto = appClassService.getTodayClasses(dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("수업 일정을 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("앱 오늘의 수업 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("수업 일정 조회 중 오류가 발생했습니다."));
        }
    }

    // 앱 수업 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<?> getClassDetail(
            @RequestParam(value = "dojangCode", required = true) String dojangCode,
            @RequestParam(value = "classCode", required = true) String classCode) {
        
        try {
            ClassDetailRespDto respDto = appClassService.getClassDetail(dojangCode, classCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("수업 상세를 조회했습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.error("앱 수업 상세 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("앱 수업 상세 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("수업 상세 조회 중 오류가 발생했습니다."));
        }
    }
}