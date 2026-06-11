package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.billing.reqDto.BillingListReqDto;
import com.taekwondo.miwool.dto.billing.reqDto.ProcessPaymentReqDto;
import com.taekwondo.miwool.dto.billing.respDto.BillingListRespDto;
import com.taekwondo.miwool.entity.MonthlyBilling;
import com.taekwondo.miwool.entity.TuitionPayment;
import com.taekwondo.miwool.repository.MonthlyBillingRepository;
import com.taekwondo.miwool.repository.TuitionPaymentRepository;
import com.taekwondo.miwool.util.AgeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingService {

    private final MonthlyBillingRepository monthlyBillingRepository;
    private final TuitionPaymentRepository tuitionPaymentRepository;

    // 청구서 목록 조회
    @Transactional(readOnly = true)
    public List<BillingListRespDto> getBillingList(String dojangCode, BillingListReqDto reqDto) {
        log.info("청구서 목록 조회: dojangCode={}, {}", dojangCode, reqDto);
        
        LocalDate startDate = null;
        LocalDate endDate = null;
        
        if (reqDto.getStartDate() != null && !reqDto.getStartDate().isEmpty()) {
            startDate = LocalDate.parse(reqDto.getStartDate(), 
                DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        
        if (reqDto.getEndDate() != null && !reqDto.getEndDate().isEmpty()) {
            endDate = LocalDate.parse(reqDto.getEndDate(), 
                DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        
        // Repository 조회 (Object[] 반환) - dojangCode 추가
        List<Object[]> rawResults = monthlyBillingRepository.searchBillingList(
            dojangCode,  // 추가
            reqDto.getStudentSearch(),
            reqDto.getBillingStatus(),
            startDate,
            endDate
        );
        
        // DTO 변환 (나머지 동일)
        List<BillingListRespDto> result = rawResults.stream()
                .map(row -> {
                    java.sql.Date birthDateSql = (java.sql.Date) row[3];
                    LocalDate birthDate = birthDateSql.toLocalDate();
                    int age = AgeUtil.calculateKoreanAge(birthDate);
                    
                    return BillingListRespDto.builder()
                        .studentCode((String) row[0])
                        .genderCode((Integer) row[1])
                        .studentName((String) row[2])
                        .age(age)
                        .grade((String) row[4])
                        .beltCode((String) row[5])
                        .beltName((String) row[6])
                        .ropeBeltCode((String) row[7])
                        .ropeBeltName((String) row[8])
                        .billingCode((Integer) row[9])
                        .billingAmount((Integer) row[10])
                        .billingDate(row[11] != null ? ((java.sql.Date) row[11]).toLocalDate() : null)
                        .billingStatus((String) row[12])
                        .paidAt(row[13] != null ? ((java.sql.Timestamp) row[13]).toLocalDateTime() : null)
                        .paymentMethod((String) row[14])
                        .actualPaymentAmount(row[15] != null ? ((Number) row[15]).intValue() : null)
                        .receiptPhone((String) row[16])
                        .note((String) row[17])
                        .build();
                })
                .toList();
        
        log.info("청구서 목록 조회 완료: {}건", result.size());
        return result;
    }

    // 납부 처리
    @Transactional
    public void processPayment(ProcessPaymentReqDto reqDto) {
        log.info("납부 처리 시작: billingCode={}", reqDto.getBillingCode());
        
        // 1. 청구서 조회
        MonthlyBilling billing = monthlyBillingRepository
            .findById(reqDto.getBillingCode())
            .orElseThrow(() -> new IllegalArgumentException("청구서를 찾을 수 없습니다"));
        
        // 2. 이미 납부완료인지 확인
        if ("납부완료".equals(billing.getBillingStatus())) {
            throw new IllegalStateException("이미 납부완료된 청구서입니다");
        }
        
        // 3. 납부 내역 저장 (금액 불일치 검증 제거, receiptPhone/note 추가)
        TuitionPayment payment = TuitionPayment.builder()
            .billingCode(reqDto.getBillingCode())
            .paymentDate(reqDto.getPaymentDate())
            .paymentAmount(reqDto.getPaymentAmount())
            .paymentMethod(reqDto.getPaymentMethod())
            .receiptPhone(reqDto.getReceiptPhone())  // ← 추가!
            .note(reqDto.getNote())                  // ← 추가!
            .build();
        
        tuitionPaymentRepository.save(payment);
        log.info("납부 내역 저장 완료: paymentCode={}", payment.getPaymentCode());
        
        // 4. 청구서 상태 업데이트 (금액 무관하게 납부완료)
        billing.setBillingStatus("납부완료");
        billing.setPaidAt(LocalDateTime.now());
        monthlyBillingRepository.save(billing);
        
        log.info("납부 처리 완료: billingCode={}, actualAmount={}, billingAmount={}",
            reqDto.getBillingCode(), reqDto.getPaymentAmount(), billing.getBillingAmount());
    }
    
    // 납부 취소
    @Transactional
    public void cancelPayment(Integer billingCode) {
        log.info("납부 취소 시작: billingCode={}", billingCode);
        
        // 1. 청구서 조회
        MonthlyBilling billing = monthlyBillingRepository
            .findById(billingCode)
            .orElseThrow(() -> new IllegalArgumentException("청구서를 찾을 수 없습니다"));
        
        // 2. 납부완료 상태 확인
        if (!"납부완료".equals(billing.getBillingStatus())) {
            throw new IllegalStateException("납부완료 상태가 아닙니다");
        }
        
        // 3. tuition_payment 삭제
        tuitionPaymentRepository.deleteByBillingCode(billingCode);
        log.info("납부 내역 삭제 완료: billingCode={}", billingCode);
        
        // 4. 청구서 상태 원복
        billing.setBillingStatus("미납");
        billing.setPaidAt(null);
        monthlyBillingRepository.save(billing);
        
        log.info("납부 취소 완료: billingCode={}", billingCode);
    }
    
}