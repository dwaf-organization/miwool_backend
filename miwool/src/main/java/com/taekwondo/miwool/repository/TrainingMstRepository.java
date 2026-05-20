package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.TrainingMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingMstRepository extends JpaRepository<TrainingMst, String> {
    
    /**
     * 도장별 패키지 목록 조회 (최신순)
     */
    List<TrainingMst> findByDojangCodeOrderByCreatedAtDesc(String dojangCode);
    
    /**
     * 도장별 패키지 목록 조회 (과거순)
     */
    List<TrainingMst> findByDojangCodeOrderByPackageCodeAsc(String dojangCode);
    
    /**
     * packageCode 자동 생성용
     * 형식: MW26001-PKG001
     */
    Optional<TrainingMst> findFirstByPackageCodeStartingWithOrderByPackageCodeDesc(String prefix);
    
    /**
     * 패키지별 제자수 및 매출 조회
     */
    @Query(value = """
        SELECT 
          tm.package_name,
          COUNT(DISTINCT st.student_code) as student_count,
          COALESCE(SUM(mb.billing_amount), 0) as revenue
        FROM student_training st
        INNER JOIN training_mst tm ON st.package_code = tm.package_code
        INNER JOIN student_mst sm ON st.student_code = sm.student_code
        LEFT JOIN monthly_billing mb ON st.student_code = mb.student_code 
            AND mb.billing_month = :month
            AND st.training_info_code = mb.training_info_code
        WHERE sm.dojang_code = :dojangCode
          AND sm.is_deleted = 0
          AND st.is_current = 1
        GROUP BY tm.package_name
        ORDER BY student_count DESC
        """, nativeQuery = true)
    List<Object[]> findPackageStats(
            @Param("dojangCode") String dojangCode,
            @Param("month") String month);
    
    // 패키지 목록 및 수강생 수 (use_yn='Y'인 것만)
    // (package_code, package_name, weekly_count, base_price, use_yn, student_count)
    @Query(value = 
        "SELECT " +
        "    t.package_code, " +
        "    t.package_name, " +
        "    t.weekly_count, " +
        "    t.base_price, " +
        "    t.use_yn, " +
        "    COUNT(DISTINCT st.student_code) AS student_count " +
        "FROM training_mst t " +
        "LEFT JOIN student_training st ON t.package_code = st.package_code " +
        "    AND st.is_current = 1 " +
        "WHERE t.dojang_code = :dojangCode " +
//        "AND t.use_yn = 'Y' " +
        "GROUP BY t.package_code, t.package_name, t.weekly_count, t.base_price, t.use_yn " +
        "ORDER BY t.package_code",
        nativeQuery = true)
    List<Object[]> findPackagesWithStudentCount(@Param("dojangCode") String dojangCode);
    
    
}