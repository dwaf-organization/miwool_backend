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
                    .billingCode((Integer) row[7])
                    .billingAmount((Integer) row[8])
                    .billingDate(row[9] != null ? ((java.sql.Date) row[9]).toLocalDate() : null)
                    .billingStatus((String) row[10])
                    .paidAt(row[11] != null ? ((java.sql.Timestamp) row[11]).toLocalDateTime() : null)
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
        
        // 3. 납부금액 검증
        if (!reqDto.getPaymentAmount().equals(billing.getBillingAmount())) {
            throw new IllegalArgumentException(
                String.format("청구금액(%d원)과 납부금액(%d원)이 일치하지 않습니다", 
                    billing.getBillingAmount(), reqDto.getPaymentAmount()));
        }
        
        // 4. 납부 내역 저장
        TuitionPayment payment = TuitionPayment.builder()
            .billingCode(reqDto.getBillingCode())
            .paymentDate(reqDto.getPaymentDate())
            .paymentAmount(reqDto.getPaymentAmount())
            .paymentMethod(reqDto.getPaymentMethod())
            .build();
        
        tuitionPaymentRepository.save(payment);
        log.info("납부 내역 저장 완료: paymentCode={}", payment.getPaymentCode());
        
        // 5. 청구서 상태 업데이트
        billing.setBillingStatus("납부완료");
        billing.setPaidAt(LocalDateTime.now());
        monthlyBillingRepository.save(billing);
        
        log.info("납부 처리 완료: billingCode={}, paymentAmount={}", 
            reqDto.getBillingCode(), reqDto.getPaymentAmount());
    }
}