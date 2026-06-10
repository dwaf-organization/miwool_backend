package com.taekwondo.miwool.service.app;

import com.taekwondo.miwool.dto.app.billing.reqDto.ConfirmPaymentReqDto;
import com.taekwondo.miwool.dto.app.billing.respDto.BillingStatusRespDto;
import com.taekwondo.miwool.dto.app.billing.respDto.BillingStudentDto;
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
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppBillingService {

    private final MonthlyBillingRepository monthlyBillingRepository;
    private final TuitionPaymentRepository tuitionPaymentRepository;

    /**
     * 앱 납부현황 조회
     */
    @Transactional(readOnly = true)
    public BillingStatusRespDto getBillingStatus(String dojangCode, String month, String paymentStatus) {
        log.info("앱 납부현황 조회: dojangCode={}, month={}, paymentStatus={}", dojangCode, month, paymentStatus);
        
        // 1. 상단 카드 데이터 조회
        List<Object[]> cardDataList = monthlyBillingRepository.findBillingCardData(dojangCode, month);
        Object[] cardData = cardDataList.get(0);
        
        Integer completedCount = ((Number) cardData[0]).intValue();
        Integer completedRevenue = ((Number) cardData[1]).intValue();
        Integer unpaidCount = ((Number) cardData[2]).intValue();
        Integer unpaidRevenue = ((Number) cardData[3]).intValue();
        
        // 2. 제자 리스트 조회
        List<Object[]> results = monthlyBillingRepository.findBillingStudentList(dojangCode, month, paymentStatus);
        
        // 3. DTO 변환
        List<BillingStudentDto> students = results.stream()
                .map(row -> {
                    Integer billingCode = (Integer) row[0];
                    Integer genderCode = (Integer) row[1];
                    String studentName = (String) row[2];
                    LocalDate birthDate = ((java.sql.Date) row[3]).toLocalDate();
                    String grade = (String) row[4];
                    Integer billingAmount = ((Number) row[5]).intValue();
                    LocalDate billingDate = ((java.sql.Date) row[6]).toLocalDate();
                    String billingStatus = (String) row[7];
                    String paymentMethod = (String) row[8];
                    Integer actualPaymentAmount = row[9] != null ? ((Number) row[9]).intValue() : null;
                    String receiptPhone = (String) row[10];
                    String note = (String) row[11];
                    
                    Integer age = AgeUtil.calculateKoreanAge(birthDate);
                    
                    return BillingStudentDto.builder()
                            .billingCode(billingCode)
                            .genderCode(genderCode)
                            .studentName(studentName)
                            .age(age)
                            .grade(grade)
                            .billingAmount(billingAmount)
                            .billingDate(billingDate)
                            .billingStatus(billingStatus)
                            .paymentMethod(paymentMethod)
                            .actualPaymentAmount(actualPaymentAmount)
                            .receiptPhone(receiptPhone)
                            .note(note)
                            .build();
                })
                .collect(Collectors.toList());
        
        log.info("앱 납부현황 조회 완료: 제자 수={}", students.size());
        
        return BillingStatusRespDto.builder()
                .yearMonth(month)
                .completedCount(completedCount)
                .completedRevenue(completedRevenue)
                .unpaidCount(unpaidCount)
                .unpaidRevenue(unpaidRevenue)
                .students(students)
                .build();
    }

    /**
     * 앱 납부처리
     */
    @Transactional
    public void confirmPayment(ConfirmPaymentReqDto reqDto) {
        log.info("앱 납부처리: billingCode={}, paidAmount={}", reqDto.getBillingCode(), reqDto.getPaidAmount());
        
        // 1. monthly_billing 조회
        MonthlyBilling monthlyBilling = monthlyBillingRepository.findById(reqDto.getBillingCode())
                .orElseThrow(() -> new IllegalArgumentException("청구 정보를 찾을 수 없습니다."));
        
        // 2. 이미 납부완료인지 확인
        if ("납부완료".equals(monthlyBilling.getBillingStatus())) {
            throw new IllegalArgumentException("이미 납부완료된 청구서입니다.");
        }
        
        // 3. tuition_payment INSERT
        TuitionPayment tuitionPayment = TuitionPayment.builder()
                .billingCode(reqDto.getBillingCode())
                .paymentMethod(reqDto.getPaymentMethod())
                .paymentAmount(reqDto.getPaidAmount())
                .paymentDate(reqDto.getPaidAt())
                .receiptPhone(reqDto.getReceiptPhone())
                .note(reqDto.getNote())
                .build();
        tuitionPaymentRepository.save(tuitionPayment);
        
        // 4. monthly_billing UPDATE
        monthlyBilling.setBillingStatus("납부완료");
        monthlyBilling.setPaidAt(reqDto.getPaidAt().atStartOfDay());
        monthlyBillingRepository.save(monthlyBilling);
        
        log.info("앱 납부처리 완료: billingCode={}", reqDto.getBillingCode());
    }
    
    // 납부취소
    @Transactional
    public void cancelPayment(Integer billingCode) {
        log.info("앱 납부취소: billingCode={}", billingCode);
        
        // 1. monthly_billing 조회
        MonthlyBilling monthlyBilling = monthlyBillingRepository.findById(billingCode)
                .orElseThrow(() -> new IllegalArgumentException("청구 정보를 찾을 수 없습니다."));
        
        // 2. 납부완료 상태 확인
        if (!"납부완료".equals(monthlyBilling.getBillingStatus())) {
            throw new IllegalArgumentException("납부완료 상태가 아닙니다.");
        }
        
        // 3. tuition_payment 삭제
        tuitionPaymentRepository.deleteByBillingCode(billingCode);
        
        // 4. monthly_billing 원복
        monthlyBilling.setBillingStatus("미납");
        monthlyBilling.setPaidAt(null);
        monthlyBillingRepository.save(monthlyBilling);
        
        log.info("앱 납부취소 완료: billingCode={}", billingCode);
    }
}