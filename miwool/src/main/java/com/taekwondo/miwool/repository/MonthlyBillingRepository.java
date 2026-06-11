package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.MonthlyBilling;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlyBillingRepository extends JpaRepository<MonthlyBilling, Integer> {
    
    /**
     * 미납 청구서 삭제 (현재월 포함 이후)
     * 퇴관 처리 시 사용
     */
    @Modifying
    @Query(value = 
        "DELETE FROM monthly_billing " +
        "WHERE student_code = :studentCode " +
        "AND billing_status = '미납' " +
        "AND billing_month >= DATE_FORMAT(NOW(), '%Y-%m')",
        nativeQuery = true)
    int deleteUnpaidFutureBillings(@Param("studentCode") String studentCode);
    
    /**
     * 수련정보코드 + 청구년월로 조회
     */
    List<MonthlyBilling> findByTrainingInfoCodeAndBillingMonth(
            Integer trainingInfoCode, String billingMonth);
    
    /**
     * 수련정보코드 + 청구상태로 조회
     */
    List<MonthlyBilling> findByTrainingInfoCodeAndBillingStatus(
            Integer trainingInfoCode, String billingStatus);
    
    /**
     * 수련정보코드 + 청구년월로 삭제
     */
    @Modifying
    @Query("DELETE FROM MonthlyBilling mb WHERE mb.trainingInfoCode = :trainingInfoCode AND mb.billingMonth = :billingMonth")
    void deleteByTrainingInfoCodeAndBillingMonth(
            @Param("trainingInfoCode") Integer trainingInfoCode, 
            @Param("billingMonth") String billingMonth);
    
    /**
     * 수련정보코드 + 청구년월 + 청구상태(제외)로 삭제
     */
    @Modifying
    @Query("DELETE FROM MonthlyBilling mb WHERE mb.trainingInfoCode = :trainingInfoCode AND mb.billingMonth = :billingMonth AND mb.billingStatus != :excludeStatus")
    void deleteByTrainingInfoCodeAndBillingMonthAndBillingStatusNot(
            @Param("trainingInfoCode") Integer trainingInfoCode, 
            @Param("billingMonth") String billingMonth, 
            @Param("excludeStatus") String excludeStatus);
    
    /**
     * 수련정보코드 + 청구년월로 날짜 일괄 수정
     */
    @Modifying
    @Query("UPDATE MonthlyBilling mb SET mb.billingDate = :billingDate, mb.trainingStartDate = :trainingStartDate, mb.trainingEndDate = :trainingEndDate WHERE mb.trainingInfoCode = :trainingInfoCode AND mb.billingMonth = :billingMonth")
    void updateDatesByTrainingInfoCodeAndBillingMonth(
            @Param("trainingInfoCode") Integer trainingInfoCode, 
            @Param("billingMonth") String billingMonth,
            @Param("billingDate") LocalDate billingDate,
            @Param("trainingStartDate") LocalDate trainingStartDate, 
            @Param("trainingEndDate") LocalDate trainingEndDate);
    
    /**
     * 수련정보코드 + 청구년월로 청구금액 일괄 수정
     */
    @Modifying
    @Query("UPDATE MonthlyBilling mb SET mb.billingAmount = :billingAmount WHERE mb.trainingInfoCode = :trainingInfoCode AND mb.billingMonth = :billingMonth")
    void updateBillingAmountByTrainingInfoCodeAndBillingMonth(
            @Param("trainingInfoCode") Integer trainingInfoCode, 
            @Param("billingMonth") String billingMonth, 
            @Param("billingAmount") Integer billingAmount);
    
    /**
     * 중복 청구 체크
     * 스케줄러에서 사용
     */
    boolean existsByTrainingInfoCodeAndBillingMonth(
        Integer trainingInfoCode, String billingMonth);

    // 청구서 목록 조회 (동적 검색) - dojangCode 조건 추가
    @Query(value =
            "SELECT " +
            "    s.student_code, " +
            "    s.gender_code, " +
            "    s.student_name, " +
            "    s.birth_date, " +
            "    s.grade, " +
            "    s.belt_code, " +
            "    c.code_name AS belt_name, " +
            "    s.rope_belt_code, " +                            // ← 추가! row[7]
            "    rc.code_name AS rope_belt_name, " +             // ← 추가! row[8]
            "    mb.billing_code, " +                            // 기존 row[7] → row[9]
            "    mb.billing_amount, " +
            "    mb.billing_date, " +
            "    mb.billing_status, " +
            "    mb.paid_at, " +
            "    tp.payment_method, " +
            "    tp.payment_amount AS actual_payment_amount, " +
            "    tp.receipt_phone, " +
            "    tp.note " +
            "FROM monthly_billing mb " +
            "INNER JOIN student_mst s ON mb.student_code = s.student_code " +
            "LEFT JOIN common_mst c ON s.belt_code = c.common_code " +
            "LEFT JOIN common_mst rc ON s.rope_belt_code = rc.common_code " +  // ← 추가!
            "LEFT JOIN tuition_payment tp ON mb.billing_code = tp.billing_code " +
            "WHERE s.dojang_code = :dojangCode " +
            "AND (:studentSearch IS NULL " +
            "     OR s.student_name LIKE CONCAT('%', :studentSearch, '%') " +
            "     OR s.student_code LIKE CONCAT('%', :studentSearch, '%')) " +
            "AND (:billingStatus IS NULL OR mb.billing_status = :billingStatus) " +
            "AND (:startDate IS NULL OR mb.billing_date >= :startDate) " +
            "AND (:endDate IS NULL OR mb.billing_date <= :endDate) " +
            "ORDER BY mb.billing_date DESC, s.student_name ASC",
            nativeQuery = true)
    List<Object[]> searchBillingList(
            @Param("dojangCode") String dojangCode,
            @Param("studentSearch") String studentSearch,
            @Param("billingStatus") String billingStatus,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * 대시보드 - 일별 납부완료 금액 합계
     * billing_date 기준, billing_status = '납부완료'
     * 결과: [날짜, 금액합계]
     */
    @Query(value = 
        "SELECT " +
        "    DATE(mb.billing_date) AS date, " +
        "    SUM(tp.payment_amount) AS total_amount " +
        "FROM monthly_billing mb " +
        "INNER JOIN student_mst s ON mb.student_code = s.student_code " +
        "INNER JOIN tuition_payment tp ON mb.billing_code = tp.billing_code " +
        "WHERE mb.billing_status = '납부완료' " +
        "AND s.dojang_code = :dojangCode " +
        "AND DATE_FORMAT(mb.billing_date, '%Y%m') = :month " +
        "GROUP BY DATE(mb.billing_date) " +
        "ORDER BY date ASC",
        nativeQuery = true)
    List<Object[]> getDailyPaidAmount(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 대시보드 - 월별 청구 총액 (상태 무관)
     * billing_month 기준, 모든 청구서 합계
     */
    @Query(value = 
        "SELECT COALESCE(SUM(mb.billing_amount), 0) " +
        "FROM monthly_billing mb " +
        "INNER JOIN student_mst s ON mb.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND mb.billing_month = :billingMonth",
        nativeQuery = true)
    int getMonthlyBillingTotal(
        @Param("dojangCode") String dojangCode,
        @Param("billingMonth") String billingMonth);
    
    /**
     * 통계 - 패키지분포 (현재월 청구서 기준)
     * 결과: [패키지코드, 패키지명, 인원수]
     */
    @Query(value = 
        "SELECT " +
        "    tm.package_code, " +
        "    tm.package_name, " +
        "    COUNT(DISTINCT mb.student_code) AS student_count " +
        "FROM monthly_billing mb " +
        "INNER JOIN student_training st ON mb.training_info_code = st.training_info_code " +
        "INNER JOIN training_mst tm ON st.package_code = tm.package_code " +
        "INNER JOIN student_mst s ON mb.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND mb.billing_month = :billingMonth " +
        "GROUP BY tm.package_code, tm.package_name " +
        "ORDER BY student_count DESC",
        nativeQuery = true)
    List<Object[]> getPackageDistribution(
        @Param("dojangCode") String dojangCode,
        @Param("billingMonth") String billingMonth);
    
    /**
     * 제자관리결산 - 납부완료 금액 합계
     */
    @Query(value = 
		"SELECT COALESCE(SUM(tp.payment_amount), 0) " +
        "FROM monthly_billing mb " +
        "INNER JOIN student_mst s ON mb.student_code = s.student_code " +
        "INNER JOIN tuition_payment tp ON mb.billing_code = tp.billing_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND mb.billing_month = :billingMonth " +
        "AND mb.billing_status = '납부완료'",
        nativeQuery = true)
    int getPaidAmount(
        @Param("dojangCode") String dojangCode,
        @Param("billingMonth") String billingMonth);
    
    /**
     * 제자관리결산 - 미납 금액 합계
     */
    @Query(value = 
        "SELECT COALESCE(SUM(mb.billing_amount), 0) " +
        "FROM monthly_billing mb " +
        "INNER JOIN student_mst s ON mb.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND mb.billing_month = :billingMonth " +
        "AND mb.billing_status = '미납'",
        nativeQuery = true)
    int getUnpaidAmount(
        @Param("dojangCode") String dojangCode,
        @Param("billingMonth") String billingMonth);
    
    /**
     * 제자관리결산 - 패키지별 납부완료 금액
     * 결과: [패키지코드, 패키지명, 인원수, 매출]
     */
    @Query(value = 
        "SELECT " +
        "    tm.package_code, " +
        "    tm.package_name, " +
        "    COUNT(DISTINCT mb.student_code) AS student_count, " +
        "    COALESCE(SUM(tp.payment_amount), 0) AS revenue " +
        "FROM monthly_billing mb " +
        "INNER JOIN student_training st ON mb.training_info_code = st.training_info_code " +
        "INNER JOIN training_mst tm ON st.package_code = tm.package_code " +
        "INNER JOIN student_mst s ON mb.student_code = s.student_code " +
        "INNER JOIN tuition_payment tp ON mb.billing_code = tp.billing_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND mb.billing_month = :billingMonth " +
        "AND mb.billing_status = '납부완료' " +
        "GROUP BY tm.package_code, tm.package_name " +
        "ORDER BY revenue DESC",
        nativeQuery = true)
    List<Object[]> getPackageRevenue(
        @Param("dojangCode") String dojangCode,
        @Param("billingMonth") String billingMonth);
    
    /**
     * 제자관리결산 - 성별 매출
     * 결과: [성별코드, 매출]
     */
    @Query(value = 
        "SELECT " +
        "    s.gender_code, " +
        "    COALESCE(SUM(tp.payment_amount), 0) AS revenue " +
        "FROM monthly_billing mb " +
        "INNER JOIN student_mst s ON mb.student_code = s.student_code " +
        "INNER JOIN tuition_payment tp ON mb.billing_code = tp.billing_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND mb.billing_month = :billingMonth " +
        "AND mb.billing_status = '납부완료' " +
        "GROUP BY s.gender_code " +
        "ORDER BY s.gender_code",
        nativeQuery = true)
    List<Object[]> getGenderRevenue(
        @Param("dojangCode") String dojangCode,
        @Param("billingMonth") String billingMonth);
    
    /**
     * 제자관리결산 - 연령대별 매출
     * 결과: [연령대, 매출]
     */
    @Query(value = 
            "SELECT " +
            "    CASE " +
            "        WHEN (YEAR(CURDATE()) - YEAR(s.birth_date) + 1) BETWEEN 1 AND 7 THEN '유아부' " +
            "        WHEN (YEAR(CURDATE()) - YEAR(s.birth_date) + 1) BETWEEN 8 AND 13 THEN '초등부' " +
            "        WHEN (YEAR(CURDATE()) - YEAR(s.birth_date) + 1) BETWEEN 14 AND 16 THEN '중등부' " +
            "        WHEN (YEAR(CURDATE()) - YEAR(s.birth_date) + 1) BETWEEN 17 AND 19 THEN '고등부' " +
            "        ELSE '성인부' " +
            "    END AS age_group, " +
            "    COALESCE(SUM(tp.payment_amount), 0) AS revenue " +  
            "FROM monthly_billing mb " +
            "INNER JOIN student_mst s ON mb.student_code = s.student_code " +
            "INNER JOIN tuition_payment tp ON mb.billing_code = tp.billing_code " +
            "WHERE s.dojang_code = :dojangCode " +
            "AND mb.billing_month = :billingMonth " +
            "AND mb.billing_status = '납부완료' " +
            "GROUP BY age_group " +
            "ORDER BY " +
            "    CASE age_group " +
            "        WHEN '유아부' THEN 1 " +
            "        WHEN '초등부' THEN 2 " +
            "        WHEN '중등부' THEN 3 " +
            "        WHEN '고등부' THEN 4 " +
            "        WHEN '성인부' THEN 5 " +
            "    END",
            nativeQuery = true)
        List<Object[]> getAgeRevenue(
            @Param("dojangCode") String dojangCode,
            @Param("billingMonth") String billingMonth);
    
    /**
     * 앱 제자 상세 - 이번달 청구 정보 조회
     * 결과: [청구상태, 납부일, 청구일]
     */
    @Query(value = 
        "SELECT " +
        "    SUM(billing_amount) AS total_amount, " +
        "    SUM(CASE WHEN billing_status != '완납' THEN 1 ELSE 0 END) AS unpaid_count, " +
        "    MAX(paid_at) AS latest_paid_at, " +
        "    MIN(billing_date) AS earliest_billing_date " +
        "FROM monthly_billing " +
        "WHERE student_code = :studentCode " +
        "AND billing_month = :billingMonth",
        nativeQuery = true)
    List<Object[]> findBillingByMonthSum(
        @Param("studentCode") String studentCode,
        @Param("billingMonth") String billingMonth);

    // 상단 카드 데이터 조회
    @Query(value = """
            SELECT 
                COUNT(DISTINCT CASE WHEN mb.billing_status = '납부완료' THEN mb.student_code END) as completed_count,
                COALESCE(SUM(CASE WHEN mb.billing_status = '납부완료' THEN tp.payment_amount ELSE 0 END), 0) as completed_revenue,
                COUNT(DISTINCT CASE WHEN mb.billing_status = '미납' THEN mb.student_code END) as unpaid_count,
                COALESCE(SUM(CASE WHEN mb.billing_status = '미납' THEN mb.billing_amount ELSE 0 END), 0) as unpaid_revenue
            FROM monthly_billing mb
            INNER JOIN student_mst s ON mb.student_code = s.student_code
            LEFT JOIN tuition_payment tp ON mb.billing_code = tp.billing_code
            WHERE s.dojang_code = :dojangCode
              AND mb.billing_month = :month
            """, nativeQuery = true)
        List<Object[]> findBillingCardData(
                @Param("dojangCode") String dojangCode,
                @Param("month") String month);
 
    // 제자 납부 리스트 조회
    @Query(value =
    	    "SELECT " +
    	    "    mb.billing_code, " +
    	    "    s.gender_code, " +
    	    "    s.student_name, " +
    	    "    s.birth_date, " +
    	    "    s.grade, " +
    	    "    mb.billing_amount, " +
    	    "    mb.billing_date, " +
    	    "    mb.billing_status, " +
    	    "    tp.payment_method, " +
    	    "    tp.payment_amount AS actual_payment_amount, " +
    	    "    tp.receipt_phone, " +
    	    "    tp.note " +
    	    "FROM monthly_billing mb " +
    	    "INNER JOIN student_mst s ON mb.student_code = s.student_code " +
    	    "LEFT JOIN tuition_payment tp ON mb.billing_code = tp.billing_code " +
    	    "WHERE s.dojang_code = :dojangCode " +
    	    "AND mb.billing_month = :month " +
    	    "AND (:paymentStatus = '전체' OR mb.billing_status = :paymentStatus) " +
    	    "ORDER BY mb.billing_status ASC, s.student_name ASC",
    	    nativeQuery = true)
    	List<Object[]> findBillingStudentList(
    	        @Param("dojangCode") String dojangCode,
    	        @Param("month") String month,
    	        @Param("paymentStatus") String paymentStatus);
    
    /**
     * 특정 월의 총 매출 조회
     */
    @Query(value = """
        SELECT SUM(billing_amount)
        FROM monthly_billing
        WHERE billing_month = :billingMonth AND billing_status = :billingStatus
        """, nativeQuery = true)
    Long sumPaymentAmountByBillingMonthAndBillingStatus(
            @Param("billingMonth") String billingMonth,
            @Param("billingStatus") String billingStatus);
 
    /**
     * 매출 TOP 5 도장 조회
     */
    @Query(value = """
        SELECT d.dojang_name, SUM(mb.billing_amount) as revenue, d.dojang_code
        FROM taekwondo_mst d
        LEFT JOIN student_mst s ON d.dojang_code = s.dojang_code
        LEFT JOIN monthly_billing mb ON s.student_code = mb.student_code
        WHERE mb.billing_month = :billingMonth AND mb.billing_status = '납부완료'
        GROUP BY d.dojang_code, d.dojang_name
        ORDER BY revenue DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findTop5RevenueByMonth(@Param("billingMonth") String billingMonth);
 
    /**
     * 특정 도장의 특정 월 매출 조회
     */
    @Query(value = """
        SELECT SUM(mb.billing_amount)
        FROM monthly_billing mb
        JOIN student_mst s ON mb.student_code = s.student_code
        WHERE s.dojang_code = :dojangCode 
          AND mb.billing_month = :billingMonth 
          AND mb.billing_status = :billingStatus
        """, nativeQuery = true)
    Long sumPaymentAmountByDojangCodeAndBillingMonthAndBillingStatus(
            @Param("dojangCode") String dojangCode,
            @Param("billingMonth") String billingMonth,
            @Param("billingStatus") String billingStatus);

    /**
     * 특정 월의 총 매출 조회 (납부완료만)
     */
    @Query(value = """
        SELECT COALESCE(SUM(mb.billing_amount), 0)
        FROM monthly_billing mb
        INNER JOIN student_mst sm ON mb.student_code = sm.student_code
        WHERE sm.dojang_code = :dojangCode
          AND mb.billing_month = :month
          AND mb.billing_status = '납부완료'
        """, nativeQuery = true)
    Long sumRevenueByMonth(
            @Param("dojangCode") String dojangCode,
            @Param("month") String month);
 
    /**
     * 패키지별 제자수 및 매출 조회
     */
    @Query(value = """
        SELECT 
          p.package_name,
          COUNT(DISTINCT sp.student_code) as student_count,
          COALESCE(SUM(mb.billing_amount), 0) as revenue
        FROM student_package sp
        INNER JOIN package_mst p ON sp.package_code = p.package_code
        INNER JOIN student_mst sm ON sp.student_code = sm.student_code
        LEFT JOIN monthly_billing mb ON sp.student_code = mb.student_code 
            AND mb.billing_month = :month
        WHERE sm.dojang_code = :dojangCode
          AND sm.is_deleted = 0
          AND sp.is_deleted = 0
        GROUP BY p.package_name
        ORDER BY student_count DESC
        """, nativeQuery = true)
    List<Object[]> findPackageStats(
            @Param("dojangCode") String dojangCode,
            @Param("month") String month);
    
}