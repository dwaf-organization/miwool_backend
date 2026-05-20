package com.taekwondo.miwool.controller.admin;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.admin.alarm.respDto.SignupAlarmListRespDto;
import com.taekwondo.miwool.service.admin.AdminAlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/alarms")
@RequiredArgsConstructor
public class AdminAlarmController {

    private final AdminAlarmService adminAlarmService;

    /**
     * 관리자 가입신청 알림 목록 조회
     * GET /api/v1/admin/alarms/signup?page=0&size=20&readStatus=전체
     */
    @GetMapping("/signup")
    public ResponseEntity<?> getSignupAlarms(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "readStatus", defaultValue = "전체") String readStatus) {
        
        try {
            SignupAlarmListRespDto respDto = adminAlarmService.getSignupAlarms(readStatus, page, size);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("알림 목록을 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("관리자 가입신청 알림 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("알림 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 가입신청 알림 읽음처리
     * PUT /api/v1/admin/alarms/signup/{signupAlarmCode}/read
     */
    @PutMapping("/signup/read/{signupAlarmCode}")
    public ResponseEntity<?> markAsRead(
            @PathVariable(value = "signupAlarmCode") Integer signupAlarmCode) {
        
        try {
            adminAlarmService.markAsRead(signupAlarmCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("알림을 읽음 처리했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("가입신청 알림 읽음처리 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("가입신청 알림 읽음처리 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("알림 읽음처리 중 오류가 발생했습니다."));
        }
    }
    
}