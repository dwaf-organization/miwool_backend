package com.taekwondo.miwool.controller.app;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.app.auth.reqDto.LoginReqDto;
import com.taekwondo.miwool.dto.app.auth.respDto.LoginRespDto;
import com.taekwondo.miwool.service.app.AppAuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
@Slf4j
@RestController
@RequestMapping("/api/v1/app/auth")
@RequiredArgsConstructor
public class AppAuthController {
 
    private final AppAuthService authService;
 
    // 앱 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReqDto reqDto) {
        
        try {
            LoginRespDto respDto = authService.login(reqDto);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("로그인에 성공했습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("앱 로그인 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("로그인 중 오류가 발생했습니다."));
        }
    }
}