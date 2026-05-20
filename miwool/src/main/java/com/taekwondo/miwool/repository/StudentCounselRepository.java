package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentCounsel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentCounselRepository extends JpaRepository<StudentCounsel, String> {

    /**
     * 특정 제자의 모든 상담 이력 조회 (상담일자 내림차순)
     */
    List<StudentCounsel> findByStudentCodeOrderByCounselDateDesc(String studentCode);
    
    /**
     * 제자의 상담 목록 조회 (페이징)
     * @param studentCode 제자코드
     * @param pageable 페이징 정보 (정렬 포함)
     * @return 상담 목록 (Page)
     */
    Page<StudentCounsel> findByStudentCode(String studentCode, Pageable pageable);
    
    /**
     * 도장의 마지막 상담 코드 조회 (counselCode 자동 생성용)
     * @param prefix 코드 prefix (예: "MW26001-SC26")
     * @return 마지막 상담 (Optional)
     */
    Optional<StudentCounsel> findFirstByCounselCodeStartingWithOrderByCounselCodeDesc(String prefix);

    /**
     * 통계 - 월별 상담유형별 건수
     * 결과: [상담유형, 건수]
     */
    @Query(value = 
        "SELECT " +
        "    sc.counsel_type, " +
        "    COUNT(*) AS counsel_count " +
        "FROM student_counsel sc " +
        "INNER JOIN student_mst s ON sc.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND DATE_FORMAT(sc.counsel_date, '%Y%m') = :month " +
        "GROUP BY sc.counsel_type " +
        "ORDER BY counsel_count DESC",
        nativeQuery = true)
    List<Object[]> getCounselStats(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 앱 제자 상세 - 이번달 상담 조회
     */
    @Query(value = 
        "SELECT counsel_date " +
        "FROM student_counsel " +
        "WHERE student_code = :studentCode " +
        "AND DATE_FORMAT(counsel_date, '%Y%m') = :month " +
        "ORDER BY counsel_date DESC " +
        "LIMIT 1",
        nativeQuery = true)
    LocalDate findCounselDateByMonth(
        @Param("studentCode") String studentCode,
        @Param("month") String month);
    
    // 월별 총 상담 건수
    @Query(value = 
        "SELECT COUNT(*) FROM student_counsel sc " +
        "JOIN student_mst s ON sc.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND DATE_FORMAT(sc.counsel_date, '%Y%m') = :month",
        nativeQuery = true)
    int countTotalCounselingByMonth(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    // 상담 유형별 건수 (counsel_type, count)
    @Query(value = 
        "SELECT sc.counsel_type, COUNT(*) " +
        "FROM student_counsel sc " +
        "JOIN student_mst s ON sc.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND DATE_FORMAT(sc.counsel_date, '%Y%m') = :month " +
        "GROUP BY sc.counsel_type",
        nativeQuery = true)
    List<Object[]> countCounselingByType(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    // 미상담 제자 수 (해당 월에 상담 이력 없는 재원생)
    @Query(value = 
        "SELECT COUNT(*) FROM student_mst s " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND s.status_code = '재원' " +
        "AND NOT EXISTS ( " +
        "    SELECT 1 FROM student_counsel sc " +
        "    WHERE sc.student_code = s.student_code " +
        "    AND DATE_FORMAT(sc.counsel_date, '%Y%m') = :month " +
        ")",
        nativeQuery = true)
    int countNotCounseledStudents(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 해당 월 총 상담건수
     */
    @Query(value = 
        "SELECT COUNT(*) " +
        "FROM student_counsel " +
        "WHERE DATE_FORMAT(counsel_date, '%Y-%m') = :month",
        nativeQuery = true)
    int getTotalCounselCount(@Param("month") String month);
    
    /**
     * 해당 월 총 상담제자수 (고유 학생)
     */
    @Query(value = 
        "SELECT COUNT(DISTINCT student_code) " +
        "FROM student_counsel " +
        "WHERE DATE_FORMAT(counsel_date, '%Y-%m') = :month",
        nativeQuery = true)
    int getTotalCounseledStudents(@Param("month") String month);
    
    /**
     * 최근 12개월 상담건수 및 상담제자수
     * (month, counsel_count, counseled_students)
     */
    @Query(value = 
        "SELECT " +
        "    DATE_FORMAT(counsel_date, '%Y-%m') AS month, " +
        "    COUNT(*) AS counsel_count, " +
        "    COUNT(DISTINCT student_code) AS counseled_students " +
        "FROM student_counsel " +
        "WHERE DATE_FORMAT(counsel_date, '%Y-%m') >= " +
        "    DATE_FORMAT(DATE_SUB(STR_TO_DATE(CONCAT(:month, '-01'), '%Y-%m-%d'), INTERVAL 11 MONTH), '%Y-%m') " +
        "AND DATE_FORMAT(counsel_date, '%Y-%m') <= :month " +
        "GROUP BY DATE_FORMAT(counsel_date, '%Y-%m') " +
        "ORDER BY month",
        nativeQuery = true)
    List<Object[]> getMonthlyCounselTrend(@Param("month") String month);
    
    /**
     * 도장별 상담현황
     * (dojang_code, dojang_name, total_students, counsel_count, counseled_students)
     */
    @Query(value = 
        "SELECT " +
        "    d.dojang_code, " +
        "    d.dojang_name, " +
        "    (SELECT COUNT(*) FROM student_mst s2 " +
        "     WHERE s2.dojang_code = d.dojang_code AND s2.status_code = '재원') AS total_students, " +
        "    COUNT(sc.counsel_code) AS counsel_count, " +
        "    COUNT(DISTINCT sc.student_code) AS counseled_students " +
        "FROM taekwondo_mst d " +
        "LEFT JOIN student_mst s ON d.dojang_code = s.dojang_code " +
        "LEFT JOIN student_counsel sc ON s.student_code = sc.student_code " +
        "    AND DATE_FORMAT(sc.counsel_date, '%Y-%m') = :month " +
        "WHERE d.is_deleted = 0 " +
        "GROUP BY d.dojang_code, d.dojang_name",
        nativeQuery = true)
    List<Object[]> getDojangCounselStatus(@Param("month") String month);
    
    /**
     * 상담유형별 건수
     * (counsel_type, count)
     */
    @Query(value = 
        "SELECT counsel_type, COUNT(*) AS count " +
        "FROM student_counsel " +
        "WHERE DATE_FORMAT(counsel_date, '%Y-%m') = :month " +
        "GROUP BY counsel_type " +
        "ORDER BY count DESC",
        nativeQuery = true)
    List<Object[]> getCounselTypeStatistics(@Param("month") String month);
    
    
}