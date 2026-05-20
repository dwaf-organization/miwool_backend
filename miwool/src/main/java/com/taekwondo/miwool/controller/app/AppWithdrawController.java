package com.taekwondo.miwool.controller.app;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.app.auth.reqDto.WithdrawReqDto;
import com.taekwondo.miwool.service.app.AppWithdrawService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/app")
@RequiredArgsConstructor
public class AppWithdrawController {

    private final AppWithdrawService appWithdrawService;

    /**
     * 회원탈퇴
     * POST /api/v1/app/withdraw
     */
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody WithdrawReqDto reqDto) {
        
        try {
            appWithdrawService.withdrawAccount(reqDto.getDojangId(), reqDto.getDojangPw());
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("회원탈퇴가 완료되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("회원탈퇴 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (IllegalStateException e) {
            log.error("회원탈퇴 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("회원탈퇴 처리 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("회원탈퇴 처리 중 오류가 발생했습니다."));
        }
    }
}