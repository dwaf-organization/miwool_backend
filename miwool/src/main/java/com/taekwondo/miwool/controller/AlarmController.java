package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.alarm.respDto.AlarmRespDto;
import com.taekwondo.miwool.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    // 알림 목록 조회
    @GetMapping
    public ResponseEntity<?> getAlarmList(
            @AuthenticationPrincipal String dojangCode) {
        
        try {
            List<AlarmRespDto> result = alarmService.getAlarmList(dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("알림 목록을 조회했습니다.", result));
            
        } catch (Exception e) {
            log.error("알림 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("알림 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // 알림 읽음 처리
    @PutMapping("/read/{alarmCode}")
    public ResponseEntity<?> markAlarmAsRead(
            @AuthenticationPrincipal String dojangCode,
            @PathVariable(value = "alarmCode") Integer alarmCode) {
        
        try {
            alarmService.markAlarmAsRead(dojangCode, alarmCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("알림을 읽음 처리했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("알림 읽음 처리 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("알림 읽음 처리 중 오류가 발생했습니다."));
        }
    }
}