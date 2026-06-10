package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.billing.reqDto.BillingListReqDto;
import com.taekwondo.miwool.dto.billing.reqDto.CancelPaymentReqDto;
import com.taekwondo.miwool.dto.billing.reqDto.ProcessPaymentReqDto;
import com.taekwondo.miwool.dto.billing.respDto.BillingListRespDto;
import com.taekwondo.miwool.service.BillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    // 청구서 목록 조회
    @GetMapping("/list")
    public ResponseEntity<?> getBillingList(
            @AuthenticationPrincipal String dojangCode,
            @RequestParam(value = "studentSearch", required = false) String studentSearch,
            @RequestParam(value = "billingStatus", required = false) String billingStatus,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
        
        try {
        	String processedBillingStatus = "전체".equals(billingStatus) ? null : billingStatus;
        	
            BillingListReqDto reqDto = new BillingListReqDto(
                studentSearch, processedBillingStatus, startDate, endDate);
            
            List<BillingListRespDto> result = billingService.getBillingList(dojangCode, reqDto);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("청구서 목록을 조회했습니다.", result));
            
        } catch (Exception e) {
            log.error("청구서 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("청구서 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // 납부 처리
    @PostMapping("/payment")
    public ResponseEntity<?> processPayment(
            @Validated @RequestBody ProcessPaymentReqDto reqDto) {
        
        try {
            billingService.processPayment(reqDto);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("납부 처리가 완료되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            // 청구서 없음, 금액 불일치 등
            log.error("납부 처리 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (IllegalStateException e) {
            // 이미 완납된 청구서
            log.error("납부 처리 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("납부 처리 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("납부 처리 중 오류가 발생했습니다."));
        }
    }
    
    // 납부 취소
    @PostMapping("/payment/cancel")
    public ResponseEntity<?> cancelPayment(
            @Validated @RequestBody CancelPaymentReqDto reqDto) {
        
        try {
            billingService.cancelPayment(reqDto.getBillingCode());
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("납부가 취소되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("납부 취소 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (IllegalStateException e) {
            log.error("납부 취소 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("납부 취소 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("납부 취소 중 오류가 발생했습니다."));
        }
    }
    
}