package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentBelt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentBeltRepository extends JpaRepository<StudentBelt, String> {
    
	Optional<StudentBelt> findFirstByBeltHistoryCodeStartingWithOrderByBeltHistoryCodeDesc(String prefix);
	
    /**
     * 해당 월에 생성된 급수 이력 수 조회 (beltHistoryCode 생성용)
     */
    @Query("SELECT COUNT(b) FROM StudentBelt b WHERE b.beltHistoryCode LIKE :prefix")
    long countByBeltHistoryCodePrefix(@Param("prefix") String prefix);
    
    /**
     * 제자별 최신 급수 조회 (생성일 기준)
     */
    Optional<StudentBelt> findTopByStudentCodeOrderByCreatedAtDesc(String studentCode);
    
    /**
     * 제자코드로 급수 이력 조회
     */
    List<StudentBelt> findByStudentCode(String studentCode);
    
    /**
     * 제자의 급수/경력 이력 목록 조회 (최신순)
     */
    List<StudentBelt> findByStudentCodeOrderByAcquiredAtDesc(String studentCode);

    /**
     * 승단예정일이 있는 최신 이력 조회
     */
    Optional<StudentBelt> findTopByStudentCodeAndPromoDateIsNotNullOrderByCreatedAtDesc(String studentCode);
    
    /**
     * 해당 월 날짜별 승단예정 인원수 (달력용)
     * (promo_date, count)
     */
    @Query(value =
        "SELECT sb.promo_date, COUNT(*) AS promo_count " +
        "FROM student_belt sb " +
        "JOIN student_mst s ON sb.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND DATE_FORMAT(sb.promo_date, '%Y%m') = :month " +
        "AND sb.promo_date IS NOT NULL " +
        "AND sb.created_at = (" +
        "    SELECT MAX(sb2.created_at) " +
        "    FROM student_belt sb2 " +
        "    WHERE sb2.student_code = sb.student_code" +
        "    AND sb2.promo_date IS NOT NULL" +
        ") " +
        "GROUP BY sb.promo_date " +
        "ORDER BY sb.promo_date",
        nativeQuery = true)
    List<Object[]> getPromotionCountByMonth(
            @Param("dojangCode") String dojangCode,
            @Param("month") String month);
    
    /**
     * 특정 날짜 승단예정 제자 목록 (팝업용)
     * (student_name, gender_code, birth_date, belt_code, belt_name)
     */
    @Query(value =
        "SELECT " +
		"    s.student_code, " +
        "    s.student_name, " +
        "    s.gender_code, " +
        "    s.birth_date, " +
        "    s.belt_code, " +
        "    c.code_name AS belt_name, " +
        "    s.rope_belt_code, " +
        "    rc.code_name AS rope_belt_name " +
        "FROM student_belt sb " +
        "JOIN student_mst s ON sb.student_code = s.student_code " +
        "LEFT JOIN common_mst c ON s.belt_code = c.common_code " +
        "LEFT JOIN common_mst rc ON s.rope_belt_code = rc.common_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND sb.promo_date = :date " +
        "AND sb.created_at = (" +
        "    SELECT MAX(sb2.created_at) " +
        "    FROM student_belt sb2 " +
        "    WHERE sb2.student_code = sb.student_code" +
        "    AND sb2.promo_date IS NOT NULL" +
        ") " +
        "ORDER BY s.student_name",
        nativeQuery = true)
    List<Object[]> getPromotionStudentsByDate(
            @Param("dojangCode") String dojangCode,
            @Param("date") LocalDate date);
    
    
}