package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.TuitionPayment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TuitionPaymentRepository extends JpaRepository<TuitionPayment, Integer> {
    
    /**
     * 청구코드로 삭제
     */
    @Modifying
    @Query("DELETE FROM TuitionPayment tp WHERE tp.billingCode = :billingCode")
    void deleteByBillingCode(@Param("billingCode") Integer billingCode);
    
    /**
     * 청구코드로 납부금액 수정
     */
    @Modifying
    @Query("UPDATE TuitionPayment tp SET tp.paymentAmount = :paymentAmount WHERE tp.billingCode = :billingCode")
    void updatePaymentAmountByBillingCode(
            @Param("billingCode") Integer billingCode, 
            @Param("paymentAmount") Integer paymentAmount);
    
    /**
     * 대시보드 - 일일 납부 현황 조회
     * 결과: [납부일, 제자명, 성별코드, 생년월일, 납부방법, 납부금액]
     */
    @Query(value = 
        "SELECT " +
        "    tp.payment_date, " +
        "    s.student_name, " +
        "    s.gender_code, " +
        "    s.birth_date, " +
        "    tp.payment_method, " +
        "    tp.payment_amount " +
        "FROM tuition_payment tp " +
        "INNER JOIN monthly_billing mb ON tp.billing_code = mb.billing_code " +
        "INNER JOIN student_mst s ON mb.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND DATE_FORMAT(tp.payment_date, '%Y%m') = :month " +
        "ORDER BY tp.payment_date ASC, s.student_name ASC",
        nativeQuery = true)
    List<Object[]> getDailyPaymentList(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 대시보드 - 주차별 매출 합계
     * 일요일 시작 기준
     * 결과: [주_시작일, 매출합계]
     */
    @Query(value = 
        "SELECT " +
        "    DATE_SUB(tp.payment_date, INTERVAL (DAYOFWEEK(tp.payment_date) - 1) DAY) AS week_start, " +
        "    SUM(tp.payment_amount) AS weekly_revenue " +
        "FROM tuition_payment tp " +
        "INNER JOIN monthly_billing mb ON tp.billing_code = mb.billing_code " +
        "INNER JOIN student_mst s ON mb.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND DATE_FORMAT(tp.payment_date, '%Y%m') = :month " +
        "GROUP BY week_start " +
        "ORDER BY week_start ASC",
        nativeQuery = true)
    List<Object[]> getWeeklyRevenue(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    
}