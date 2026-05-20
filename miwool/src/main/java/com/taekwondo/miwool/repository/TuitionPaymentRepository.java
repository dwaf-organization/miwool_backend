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
    
    /**
     * 해당 월 총 납부금액 (만원 단위)
     */
    @Query(value = 
        "SELECT ROUND(SUM(tp.payment_amount) / 10000) " +
        "FROM tuition_payment tp " +
        "JOIN monthly_billing mb ON tp.billing_code = mb.billing_code " +
        "WHERE DATE_FORMAT(tp.payment_date, '%Y-%m') = :month",
        nativeQuery = true)
    Integer getTotalRevenue(@Param("month") String month);
    
    /**
     * 최근 12개월 매출 추이 (만원 단위)
     * (month, revenue)
     */
    @Query(value = 
        "SELECT " +
        "    DATE_FORMAT(tp.payment_date, '%Y-%m') AS month, " +
        "    ROUND(SUM(tp.payment_amount) / 10000) AS revenue " +
        "FROM tuition_payment tp " +
        "WHERE DATE_FORMAT(tp.payment_date, '%Y-%m') >= " +
        "    DATE_FORMAT(DATE_SUB(STR_TO_DATE(CONCAT(:month, '-01'), '%Y-%m-%d'), INTERVAL 11 MONTH), '%Y-%m') " +
        "AND DATE_FORMAT(tp.payment_date, '%Y-%m') <= :month " +
        "GROUP BY DATE_FORMAT(tp.payment_date, '%Y-%m') " +
        "ORDER BY month",
        nativeQuery = true)
    List<Object[]> getMonthlyRevenueTrend(@Param("month") String month);
    
    /**
     * 도장별 매출 현황 (상위 10개, 만원 단위)
     * (dojang_code, dojang_name, student_count, revenue, avg_fee)
     */
    @Query(value = 
        "SELECT " +
        "    d.dojang_code, " +
        "    d.dojang_name, " +
        "    COUNT(DISTINCT s.student_code) AS student_count, " +
        "    ROUND(SUM(tp.payment_amount) / 10000) AS revenue, " +
        "    ROUND(AVG(tp.payment_amount)) AS avg_fee " +
        "FROM tuition_payment tp " +
        "JOIN monthly_billing mb ON tp.billing_code = mb.billing_code " +
        "JOIN student_mst s ON mb.student_code = s.student_code " +
        "JOIN taekwondo_mst d ON s.dojang_code = d.dojang_code " +
        "WHERE DATE_FORMAT(tp.payment_date, '%Y-%m') = :month " +
        "GROUP BY d.dojang_code, d.dojang_name " +
        "ORDER BY revenue DESC " +
        "LIMIT 10",
        nativeQuery = true)
    List<Object[]> getTopDojangsByRevenue(@Param("month") String month);
    
    /**
     * 도장별 평균 교육비 (최고/최저 찾기용)
     * (dojang_name, avg_fee)
     */
    @Query(value = 
        "SELECT " +
        "    d.dojang_name, " +
        "    ROUND(AVG(tp.payment_amount)) AS avg_fee " +
        "FROM tuition_payment tp " +
        "JOIN monthly_billing mb ON tp.billing_code = mb.billing_code " +
        "JOIN student_mst s ON mb.student_code = s.student_code " +
        "JOIN taekwondo_mst d ON s.dojang_code = d.dojang_code " +
        "WHERE DATE_FORMAT(tp.payment_date, '%Y-%m') = :month " +
        "GROUP BY d.dojang_code, d.dojang_name " +
        "ORDER BY avg_fee DESC",
        nativeQuery = true)
    List<Object[]> getDojangAverageFees(@Param("month") String month);
    
    /**
     * 주 횟수별 평균 교육비
     * (weekly_count, avg_fee)
     */
    @Query(value = 
        "SELECT " +
        "    t.weekly_count, " +
        "    ROUND(AVG(tp.payment_amount)) AS avg_fee " +
        "FROM tuition_payment tp " +
        "JOIN monthly_billing mb ON tp.billing_code = mb.billing_code " +
        "JOIN student_training st ON mb.training_info_code = st.training_info_code " +
        "JOIN training_mst t ON st.package_code = t.package_code " +
        "WHERE DATE_FORMAT(tp.payment_date, '%Y-%m') = :month " +
        "GROUP BY t.weekly_count " +
        "ORDER BY t.weekly_count",
        nativeQuery = true)
    List<Object[]> getAverageFeeByWeeklyCount(@Param("month") String month);
    
    /**
     * 전체 평균 교육비
     */
    @Query(value = 
        "SELECT ROUND(AVG(tp.payment_amount)) " +
        "FROM tuition_payment tp " +
        "WHERE DATE_FORMAT(tp.payment_date, '%Y-%m') = :month",
        nativeQuery = true)
    Integer getOverallAverageFee(@Param("month") String month);
    
    
}