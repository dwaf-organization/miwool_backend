package com.taekwondo.miwool.controller.app;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.app.billing.reqDto.ConfirmPaymentReqDto;
import com.taekwondo.miwool.dto.app.billing.respDto.BillingStatusRespDto;
import com.taekwondo.miwool.service.app.AppBillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/app/billing")
@RequiredArgsConstructor
public class AppBillingController {

    private final AppBillingService appBillingService;

    // 앱 납부현황 조회
    @GetMapping
    public ResponseEntity<?> getBillingStatus(
            @RequestParam(value = "dojangCode", required = true) String dojangCode,
            @RequestParam(value = "month", required = true) String month,
            @RequestParam(value = "paymentStatus", required = false, defaultValue = "전체") String paymentStatus) {
        
        try {
            BillingStatusRespDto respDto = appBillingService.getBillingStatus(dojangCode, month, paymentStatus);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("납부현황을 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("앱 납부현황 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("납부현황 조회 중 오류가 발생했습니다."));
        }
    }

    // 앱 납부처리
    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestBody ConfirmPaymentReqDto reqDto) {
        
        try {
            appBillingService.confirmPayment(reqDto);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("납부처리가 완료되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("앱 납부처리 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("앱 납부처리 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("납부처리 중 오류가 발생했습니다."));
        }
    }
}