package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentManagementRepository extends JpaRepository<StudentManagement, String> {
    // 제자별 관리 이력 조회
    List<StudentManagement> findByStudentCodeOrderByExecutedDateDesc(String studentCode);
    
    /**
     * 교육지도 목록 조회 (Native Query)
     * 
     * 조건: yearMonth(필수), studentSearch, beltCode, genderCode, grade
     * status(실시/미실시) + category(항목) 필터링은 Service에서 처리
     */
    @Query(value = "SELECT " +
                   "  s.student_code, " +
                   "  s.gender_code, " +
                   "  s.student_name, " +
                   "  s.birth_date, " +
                   "  s.grade, " +
                   "  MAX(CASE WHEN sm.management_type_code = '전화' THEN 1 ELSE 0 END) as phone_yn, " +
                   "  MAX(CASE WHEN sm.management_type_code = '문자' THEN 1 ELSE 0 END) as message_yn, " +
                   "  MAX(CASE WHEN sm.management_type_code = '손편지' THEN 1 ELSE 0 END) as letter_yn, " +
                   "  MAX(CASE WHEN sm.management_type_code = '간식' THEN 1 ELSE 0 END) as snack_yn, " +
                   "  MAX(CASE WHEN sm.management_type_code = '영상' THEN 1 ELSE 0 END) as video_yn, " +
                   "  MAX(CASE WHEN sm.management_type_code = '상장' THEN 1 ELSE 0 END) as award_yn, " +
                   "  MAX(CASE WHEN sm.management_type_code = '관찰지' THEN 1 ELSE 0 END) as observation_yn, " +
                   "  MAX(CASE WHEN sm.management_type_code = '인바디' THEN 1 ELSE 0 END) as inbody_yn, " +
                   "  MAX(CASE WHEN sm.management_type_code = '기타' THEN 1 ELSE 0 END) as etc_yn, " +
                   "  MAX(CASE WHEN sm.management_type_code = '기타' THEN sm.note ELSE NULL END) as etc_content " +
                   "FROM student_mst s " +
                   "LEFT JOIN student_management sm ON s.student_code = sm.student_code " +
                   "  AND DATE_FORMAT(sm.executed_date, '%Y-%m') = :yearMonth " +
                   "WHERE s.dojang_code = :dojangCode " +
                   "  AND s.status_code = '재원' " +
                   "  AND (:studentSearch IS NULL " +
                   "       OR s.student_code LIKE CONCAT('%', :studentSearch, '%') " +
                   "       OR s.student_name LIKE CONCAT('%', :studentSearch, '%')) " +
                   "  AND (:beltCode IS NULL OR s.belt_code = :beltCode OR s.rope_belt_code = :beltCode) " +
                   "  AND (:genderCode IS NULL OR s.gender_code = :genderCode) " +
                   "  AND (:grade IS NULL OR s.grade = :grade) " +
                   "GROUP BY s.student_code, s.gender_code, s.student_name, s.birth_date, s.grade " +
                   "ORDER BY s.student_name ASC",
           nativeQuery = true)
    List<Object[]> findEducationManagementList(
            @Param("yearMonth") String yearMonth,
            @Param("studentSearch") String studentSearch,
            @Param("beltCode") String beltCode,
            @Param("genderCode") Integer genderCode,
            @Param("grade") String grade,
            @Param("status") String status,           // Service에서 필터링
            @Param("category") String category,       // Service에서 필터링
            @Param("dojangCode") String dojangCode);
    
    /**
     * 교육지도 일괄 저장용 - 기존 데이터 조회
     * 해당 학생의 해당 월 데이터 조회
     */
    @Query(value = "SELECT * FROM student_management " +
                   "WHERE student_code = :studentCode " +
                   "  AND DATE_FORMAT(executed_date, '%Y-%m') = :yearMonth",
           nativeQuery = true)
    List<StudentManagement> findByStudentCodeAndYearMonth(
            @Param("studentCode") String studentCode,
            @Param("yearMonth") String yearMonth);
    
    /**
     * managementCode 자동 생성용
     * 형식: MW26001-MG24001 (MG = ManaGement)
     */
    Optional<StudentManagement> findFirstByManagementCodeStartingWithOrderByManagementCodeDesc(String prefix);
    
    /**
     * 통계 - 월별 교육지도 유형별 실시 인원
     * 결과: [교육지도유형, 실시인원수]
     */
    @Query(value = 
        "SELECT " +
        "    sm.management_type_code, " +
        "    COUNT(DISTINCT sm.student_code) AS completed_count " +
        "FROM student_management sm " +
        "INNER JOIN student_mst s ON sm.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND DATE_FORMAT(sm.executed_date, '%Y%m') = :month " +
        "GROUP BY sm.management_type_code " +
        "ORDER BY completed_count DESC",
        nativeQuery = true)
    List<Object[]> getEducationStats(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 제자관리결산 - 교육지도 미실시 명단 (상위 10명)
     * 결과: [제자명, 성별코드]
     */
    @Query(value = 
        "SELECT " +
        "    s.student_name, " +
        "    s.gender_code " +
        "FROM student_mst s " +
        "LEFT JOIN ( " +
        "    SELECT DISTINCT student_code " +
        "    FROM student_management " +
        "    WHERE DATE_FORMAT(executed_date, '%Y%m') = :month " +
        ") sm ON s.student_code = sm.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND s.status_code = '재원' " +
        "AND sm.student_code IS NULL " +
        "ORDER BY s.student_name " +
        "LIMIT 10",
        nativeQuery = true)
    List<Object[]> getNotCompletedList(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 제자관리결산 - 특정 교육지도 유형 미실시 명단 (상위 10명)
     * 결과: [제자명, 성별코드]
     */
    @Query(value = 
        "SELECT " +
        "    s.student_name, " +
        "    s.gender_code " +
        "FROM student_mst s " +
        "LEFT JOIN ( " +
        "    SELECT DISTINCT student_code " +
        "    FROM student_management " +
        "    WHERE management_type_code = :guideType " +
        "    AND DATE_FORMAT(executed_date, '%Y%m') = :month " +
        ") sm ON s.student_code = sm.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND s.status_code = '재원' " +
        "AND sm.student_code IS NULL " +
        "ORDER BY s.student_name " +
        "LIMIT 16",
        nativeQuery = true)
    List<Object[]> getNotCompletedByType(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month,
        @Param("guideType") String guideType);
    
    /**
     * 도장별, 교육 유형별 실행 건수 조회 (특정 월)
     */
    @Query(value = """
        SELECT 
          s.dojang_code,
          d.dojang_name,
          sm.management_type_code,
          COUNT(DISTINCT sm.student_code) as execution_count
        FROM student_management sm
        JOIN student_mst s ON sm.student_code = s.student_code
        JOIN taekwondo_mst d ON s.dojang_code = d.dojang_code
        WHERE YEAR(sm.executed_date) = :year 
          AND MONTH(sm.executed_date) = :month
          AND sm.management_type_code IN ('전화', '문자', '손편지', '간식', '상장', '영상', '관찰지', '인바디')
        GROUP BY s.dojang_code, d.dojang_name, sm.management_type_code
        """, nativeQuery = true)
    List<Object[]> countExecutionsByDojangAndType(
            @Param("year") int year,
            @Param("month") int month);
    
    // 교육관리 항목별 진행률 (management_type_code, progress_rate)
    @Query(value = 
        "SELECT sm.management_type_code, " +
        "COUNT(DISTINCT sm.student_code) * 100.0 / ( " +
        "    SELECT COUNT(*) FROM student_mst s " +
        "    WHERE s.dojang_code = :dojangCode AND s.status_code = '재원' " +
        ") AS progress_rate " +
        "FROM student_management sm " +
        "JOIN student_mst s ON sm.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND DATE_FORMAT(sm.executed_date, '%Y%m') = :month " +
        "GROUP BY sm.management_type_code " +
        "ORDER BY sm.management_type_code",
        nativeQuery = true)
    List<Object[]> calculateGuidanceProgressByItem(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 해당 월 항목별 실행 학생 수
     * (management_type_code, executed_students)
     */
    @Query(value = 
        "SELECT " +
        "    management_type_code, " +
        "    COUNT(DISTINCT student_code) AS executed_students " +
        "FROM student_management " +
        "WHERE DATE_FORMAT(executed_date, '%Y-%m') = :month " +
        "GROUP BY management_type_code",
        nativeQuery = true)
    List<Object[]> getItemExecutionByMonth(@Param("month") String month);
    
    /**
     * 최근 3개월 항목별 실행 학생 수
     * (month, management_type_code, executed_students)
     */
    @Query(value = 
        "SELECT " +
        "    DATE_FORMAT(executed_date, '%Y-%m') AS month, " +
        "    management_type_code, " +
        "    COUNT(DISTINCT student_code) AS executed_students " +
        "FROM student_management " +
        "WHERE DATE_FORMAT(executed_date, '%Y-%m') >= " +
        "    DATE_FORMAT(DATE_SUB(STR_TO_DATE(CONCAT(:month, '-01'), '%Y-%m-%d'), INTERVAL 2 MONTH), '%Y-%m') " +
        "AND DATE_FORMAT(executed_date, '%Y-%m') <= :month " +
        "GROUP BY DATE_FORMAT(executed_date, '%Y-%m'), management_type_code " +
        "ORDER BY month, management_type_code",
        nativeQuery = true)
    List<Object[]> getMonthlyItemExecutionTrend(@Param("month") String month);
    
    /**
     * 도장별 항목별 실행 학생 수
     * (dojang_code, dojang_name, total_students, management_type_code, executed_students)
     */
    @Query(value = 
        "SELECT " +
        "    d.dojang_code, " +
        "    d.dojang_name, " +
        "    (SELECT COUNT(*) FROM student_mst s2 " +
        "     WHERE s2.dojang_code = d.dojang_code AND s2.status_code = '재원') AS total_students, " +
        "    sm.management_type_code, " +
        "    COUNT(DISTINCT sm.student_code) AS executed_students " +
        "FROM taekwondo_mst d " +
        "LEFT JOIN student_mst s ON d.dojang_code = s.dojang_code " +
        "LEFT JOIN student_management sm ON s.student_code = sm.student_code " +
        "    AND DATE_FORMAT(sm.executed_date, '%Y-%m') = :month " +
        "WHERE d.is_deleted = 0 " +
        "GROUP BY d.dojang_code, d.dojang_name, sm.management_type_code",
        nativeQuery = true)
    List<Object[]> getDojangItemExecution(@Param("month") String month);
    
    /**
     * 항목별 도장 실행률 (최저 찾기용)
     * (management_type_code, dojang_code, dojang_name, total_students, executed_students)
     */
    @Query(value = 
        "SELECT " +
        "    sm.management_type_code, " +
        "    d.dojang_code, " +
        "    d.dojang_name, " +
        "    (SELECT COUNT(*) FROM student_mst s2 " +
        "     WHERE s2.dojang_code = d.dojang_code AND s2.status_code = '재원') AS total_students, " +
        "    COUNT(DISTINCT sm.student_code) AS executed_students " +
        "FROM student_management sm " +
        "JOIN student_mst s ON sm.student_code = s.student_code " +
        "JOIN taekwondo_mst d ON s.dojang_code = d.dojang_code " +
        "WHERE DATE_FORMAT(sm.executed_date, '%Y-%m') = :month " +
        "AND d.is_deleted = 0 " +
        "GROUP BY sm.management_type_code, d.dojang_code, d.dojang_name " +
        "ORDER BY sm.management_type_code, executed_students",
        nativeQuery = true)
    List<Object[]> getItemDojangExecutionRates(@Param("month") String month);
    
    
}