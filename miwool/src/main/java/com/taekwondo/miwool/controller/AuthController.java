package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.auth.reqDto.FindIdReqDto;
import com.taekwondo.miwool.dto.auth.reqDto.LoginReqDto;
import com.taekwondo.miwool.dto.auth.reqDto.RefreshTokenReqDto;
import com.taekwondo.miwool.dto.auth.reqDto.RegisterReqDto;
import com.taekwondo.miwool.dto.auth.reqDto.ResetPwReqDto;
import com.taekwondo.miwool.dto.auth.respDto.CheckIdRespDto;
import com.taekwondo.miwool.dto.auth.respDto.FindIdRespDto;
import com.taekwondo.miwool.dto.auth.respDto.LoginRespDto;
import com.taekwondo.miwool.dto.auth.respDto.RefreshTokenRespDto;
import com.taekwondo.miwool.dto.auth.respDto.RegisterRespDto;
import com.taekwondo.miwool.dto.auth.respDto.ResetPwRespDto;
import com.taekwondo.miwool.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 회원가입
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReqDto reqDto, BindingResult bindingResult) {
        
        // Validation 에러 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            RegisterRespDto respDto = authService.register(reqDto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(RespDto.success("회원가입이 완료되었습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            // 비즈니스 로직 에러 (아이디 중복 등)
            log.warn("회원가입 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            // 예상치 못한 에러
            log.error("회원가입 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("회원가입 처리 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 아이디 중복 확인
     * GET /api/v1/auth/check-id?dojangId=miwool01
     */
    @GetMapping("/check-id")
    public ResponseEntity<?> checkId(@RequestParam String dojangId) {
        
        // 아이디 유효성 간단 체크
        if (dojangId == null || dojangId.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("아이디를 입력해주세요."));
        }
        
        try {
            CheckIdRespDto respDto = authService.checkId(dojangId);
            
            if (respDto.getAvailable()) {
                return ResponseEntity
                        .ok()
                        .body(RespDto.success("사용 가능한 아이디입니다.", respDto));
            } else {
                return ResponseEntity
                        .ok()
                        .body(RespDto.fail("이미 사용 중인 아이디입니다."));
            }
            
        } catch (Exception e) {
            log.error("아이디 중복 확인 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("아이디 확인 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 로그인
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReqDto reqDto, BindingResult bindingResult) {
        
        // Validation 에러 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            LoginRespDto respDto = authService.login(reqDto);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("로그인 성공", respDto));
            
        } catch (IllegalArgumentException e) {
            // 비즈니스 로직 에러 (아이디/비밀번호 불일치 등)
            log.warn("로그인 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            // 예상치 못한 에러
            log.error("로그인 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("로그인 처리 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 아이디 찾기
     * POST /api/v1/auth/find-id
     */
    @PostMapping("/find-id")
    public ResponseEntity<?> findId(@Valid @RequestBody FindIdReqDto reqDto, BindingResult bindingResult) {
        
        // Validation 에러 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            FindIdRespDto respDto = authService.findId(reqDto);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("아이디 찾기 성공", respDto));
            
        } catch (IllegalArgumentException e) {
            log.warn("아이디 찾기 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("아이디 찾기 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("아이디 찾기 처리 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 비밀번호 찾기 (초기화)
     * POST /api/v1/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPwReqDto reqDto, BindingResult bindingResult) {
        
        // Validation 에러 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            ResetPwRespDto respDto = authService.resetPassword(reqDto);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("비밀번호가 초기화되었습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.warn("비밀번호 초기화 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("비밀번호 초기화 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("비밀번호 초기화 처리 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 토큰 재발급
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenReqDto reqDto, BindingResult bindingResult) {
        
        // Validation 에러 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            RefreshTokenRespDto respDto = authService.refreshToken(reqDto);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("토큰이 재발급되었습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.warn("토큰 재발급 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("토큰 재발급 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("토큰 재발급 처리 중 오류가 발생했습니다."));
        }
    }

}