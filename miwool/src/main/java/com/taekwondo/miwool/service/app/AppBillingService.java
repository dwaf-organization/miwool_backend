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
                    Integer billingAmount = (Integer) row[5];
                    LocalDate billingDate = ((java.sql.Date) row[6]).toLocalDate();
                    String billingStatus = (String) row[7];
                    
                    // 나이 계산
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
        
        // 2. tuition_payment INSERT
        TuitionPayment tuitionPayment = TuitionPayment.builder()
                .billingCode(reqDto.getBillingCode())
                .paymentMethod(reqDto.getPaymentMethod())
                .paymentAmount(reqDto.getPaidAmount())
                .paymentDate(reqDto.getPaidAt())
                .build();
        tuitionPaymentRepository.save(tuitionPayment);
        
        log.info("tuition_payment 저장 완료: paymentCode={}", tuitionPayment.getPaymentCode());
        
        // 3. monthly_billing UPDATE
        monthlyBilling.setBillingStatus("납부완료");
        monthlyBilling.setPaidAt(reqDto.getPaidAt().atStartOfDay());
        monthlyBillingRepository.save(monthlyBilling);
        
        log.info("monthly_billing 업데이트 완료: billingStatus=납부완료");
        
        log.info("앱 납부처리 완료: billingCode={}", reqDto.getBillingCode());
    }
}