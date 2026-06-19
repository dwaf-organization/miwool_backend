package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.Student;
import com.taekwondo.miwool.entity.StudentBelt;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {
    
	Optional<Student> findFirstByStudentCodeStartingWithOrderByStudentCodeDesc(String prefix);
	
    /**
     * 해당 월에 생성된 제자 수 조회 (studentCode 생성용)
     */
    @Query("SELECT COUNT(s) FROM Student s WHERE s.studentCode LIKE :prefix")
    long countByStudentCodePrefix(@Param("prefix") String prefix);
    
    /**
     * 도장별 제자 전체 조회 (등록일 최신순)
     */
    List<Student> findByDojangCodeOrderByRegistDateDesc(String dojangCode);
    
    /**
     * 도장별 + 제자명/코드 검색 (등록일 최신순)
     */
    @Query("SELECT s FROM Student s WHERE s.dojangCode = :dojangCode " +
           "AND (s.studentName LIKE %:search% OR s.studentCode LIKE %:search%) " +
           "ORDER BY s.registDate DESC")
    List<Student> findByDojangCodeAndSearch(@Param("dojangCode") String dojangCode, 
                                             @Param("search") String search);
    
    /**
     * 도장별 + 제자명/코드 검색 + 성별 필터 (등록일 최신순)
     */
    @Query("SELECT s FROM Student s WHERE s.dojangCode = :dojangCode " +
           "AND (s.studentName LIKE %:search% OR s.studentCode LIKE %:search%) " +
           "AND s.genderCode = :genderCode " +
           "ORDER BY s.registDate DESC")
    List<Student> findByDojangCodeAndSearchAndGender(@Param("dojangCode") String dojangCode, 
                                                       @Param("search") String search,
                                                       @Param("genderCode") Integer genderCode);
    
    /**
     * 도장별 + 성별 필터 (등록일 최신순)
     */
    List<Student> findByDojangCodeAndGenderCodeOrderByRegistDateDesc(String dojangCode, Integer genderCode);
    
    /**
     * Native Query - 제자 목록 조회 (최신 급수만 JOIN)
     * 성능 최적화: status는 student_mst에 있으므로 JOIN 불필요
     */
    @Query(value = 
    	    "SELECT " +
    	    "    s.student_code, " +
    	    "    s.student_name, " +
    	    "    s.birth_date, " +
    	    "    s.gender_code, " +
    	    "    s.gender_name, " +
    	    "    s.grade, " +
    	    "    s.status_code, " +
    	    "    s.belt_code, " +
    	    "    s.regist_date, " +
    	    "    s.deleted_at, " +
    	    "    s.rope_belt_code " +
    	    "FROM student_mst s " +
    	    "WHERE s.dojang_code = :dojangCode " +
    	    "AND (:studentSearch IS NULL OR s.student_name LIKE CONCAT('%', :studentSearch, '%') OR s.student_code LIKE CONCAT('%', :studentSearch, '%')) " +
    	    "AND (:genderCode IS NULL OR s.gender_code = :genderCode) " +
    	    "AND (:beltCode IS NULL OR s.belt_code = :beltCode OR s.rope_belt_code = :beltCode) " +
    	    "AND (:statusCode IS NULL OR s.status_code = :statusCode) " +
    	    "AND (:gradeCode IS NULL OR s.grade = :gradeCode) " +
    	    "ORDER BY s.regist_date DESC ",
    	    nativeQuery = true)
    List<Object[]> findStudentListNative(
            @Param("dojangCode") String dojangCode,
            @Param("studentSearch") String studentSearch,
            @Param("genderCode") Integer genderCode,
            @Param("beltCode") String beltCode,
            @Param("statusCode") String statusCode,
            @Param("gradeCode") String gradeCode
    );
    
    /**
     * Native Query - 제자 목록 총 개수 조회
     */
    @Query(value = 
    	    "SELECT COUNT(*) " +
    	    "FROM student_mst s " +
    	    "WHERE s.dojang_code = :dojangCode " +
    	    "AND (:studentSearch IS NULL OR s.student_name LIKE CONCAT('%', :studentSearch, '%') OR s.student_code LIKE CONCAT('%', :studentSearch, '%')) " +
    	    "AND (:genderCode IS NULL OR s.gender_code = :genderCode) " +
    	    "AND (:beltCode IS NULL OR s.belt_code = :beltCode OR s.rope_belt_code = :beltCode) " +
    	    "AND (:statusCode IS NULL OR s.status_code = :statusCode) " +
    	    "AND (:gradeCode IS NULL OR s.grade = :gradeCode)",
    	    nativeQuery = true)
    long countStudentListNative(
            @Param("dojangCode") String dojangCode,
            @Param("studentSearch") String studentSearch,
            @Param("genderCode") Integer genderCode,
            @Param("beltCode") String beltCode,
            @Param("statusCode") String statusCode,
            @Param("gradeCode") String gradeCode
    );
    
    /**
     * 제자 선택 팝업 조회 (Native Query)
     * 조건: 제자명(코드/명 부분조회), 급수, 성별, 학년
     * 재원 상태인 학생만 조회
     * 급수명 포함
     */
    @Query(value = "SELECT " +
                   "    s.student_code, " +
                   "    s.student_name, " +
                   "    s.gender_code, " +
                   "    s.birth_date, " +
                   "    s.grade, " +
                   "    s.belt_code, " +
                   "    c.code_name AS belt_name, " +
                   "    s.rope_belt_code, " +
                   "    rc.code_name AS rope_belt_name " +
                   "FROM student_mst s " +
                   "LEFT JOIN common_mst c ON s.belt_code = c.common_code " +
                   "LEFT JOIN common_mst rc ON s.rope_belt_code = rc.common_code " +
                   "WHERE s.dojang_code = :dojangCode " +
                   "  AND s.status_code IN ('재원', '복관') " +
                   "  AND (:studentSearch IS NULL " +
                   "       OR s.student_code LIKE CONCAT('%', :studentSearch, '%') " +
                   "       OR s.student_name LIKE CONCAT('%', :studentSearch, '%')) " +
                   "  AND (:beltCode IS NULL OR s.belt_code = :beltCode OR s.rope_belt_code = :beltCode) " +
                   "  AND (:genderCode IS NULL OR s.gender_code = :genderCode) " +
                   "  AND (:grade IS NULL OR s.grade = :grade) " +
                   "ORDER BY s.student_name ASC",
           nativeQuery = true)
    List<Object[]> findStudentsForSelection(
            @Param("studentSearch") String studentSearch,
            @Param("beltCode") String beltCode,
            @Param("genderCode") Integer genderCode,
            @Param("grade") String grade,
            @Param("dojangCode") String dojangCode);

    /**
     * 대시보드 - 월별 입관생 수
     * student_status에서 입관일 당일 '재원' 이력 확인
     * 같은 날짜에 재원/체험 여러 번 변경 시 가장 최신 상태만 카운트 (퇴관은 별도)
     */
    @Query(value = 
        "SELECT COUNT(DISTINCT ss.student_code) " +
        "FROM student_status ss " +
        "JOIN student_mst s ON ss.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND DATE_FORMAT(s.regist_date, '%Y%m') = :month " +
        "AND ss.status_code = '재원' " +
        "AND DATE(ss.change_date) = DATE(s.regist_date) " +
        "AND ss.created_at = ( " +
        "    SELECT MAX(created_at) " +
        "    FROM student_status " +
        "    WHERE student_code = s.student_code " +
        "    AND DATE(change_date) = DATE(s.regist_date) " +
        "    AND status_code IN ('재원', '체험') " +
        ")",
        nativeQuery = true)
    int countMonthlyEnrollment(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 대시보드 - 월별 휴관생 수 (해당 월에 휴관 이력이 생긴 인원)
     */
    @Query(value = 
        "SELECT COUNT(DISTINCT ss.student_code) " +
        "FROM student_status ss " +
        "JOIN student_mst s ON ss.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND ss.status_code = '휴관' " +
        "AND DATE_FORMAT(ss.change_date, '%Y%m') = :month",
        nativeQuery = true)
    int countMonthlySuspension(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
 
    /**
     * 대시보드 - 월별 복관생 수 (해당 월에 복관 이력이 생긴 인원)
     */
    @Query(value = 
        "SELECT COUNT(DISTINCT ss.student_code) " +
        "FROM student_status ss " +
        "JOIN student_mst s ON ss.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND ss.status_code = '복관' " +
        "AND DATE_FORMAT(ss.change_date, '%Y%m') = :month",
        nativeQuery = true)
    int countMonthlyReinstatement(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 대시보드 - 월별 퇴관생 수
     * student_status 테이블에서 퇴관 이력 조회
     */
    @Query(value = 
        "SELECT COUNT(DISTINCT ss.student_code) " +
        "FROM student_status ss " +
        "JOIN student_mst s ON ss.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND ss.status_code = '퇴관' " +
        "AND DATE_FORMAT(ss.change_date, '%Y%m') = :month",
        nativeQuery = true)
    int countMonthlyWithdrawal(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 대시보드 - 현재 총원 (재원 + 체험)
     */
    @Query(value = 
        "SELECT COUNT(*) FROM student_mst " +
        "WHERE dojang_code = :dojangCode " +
        "AND status_code IN ('재원', '체험', '복관')",
        nativeQuery = true)
    int countStatisticCurrentTotal(@Param("dojangCode") String dojangCode);
    
    /**
     * 대시보드 - 해당 월 말일 기준 총원 (재원 + 체험)
     * 해당 월까지 입관한 학생 중 해당 월까지 퇴관하지 않은 학생
     */
    @Query(value = 
        "SELECT COUNT(*) FROM student_mst s " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND DATE_FORMAT(s.regist_date, '%Y%m') <= :month " +
        "AND NOT EXISTS ( " +
        "    SELECT 1 FROM student_status ss " +
        "    WHERE ss.student_code = s.student_code " +
        "    AND ss.status_code = '퇴관' " +
        "    AND DATE_FORMAT(ss.change_date, '%Y%m') <= :month " +
        ")",
        nativeQuery = true)
    int countCurrentTotal(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 대시보드 - 일별 입관/퇴관/체험/휴관/복관 수 조회
     * 결과: [날짜, 입관수, 퇴관수, 체험수, 휴관수, 복관수]
     */
    @Query(value = 
        "SELECT " +
        "    dates.date, " +
        "    COALESCE(enroll.cnt, 0) AS enrollment, " +
        "    COALESCE(withdraw.cnt, 0) AS withdrawal, " +
        "    COALESCE(trial.cnt, 0) AS trial, " +
        "    COALESCE(suspension.cnt, 0) AS suspension, " +    // ← 추가!
        "    COALESCE(reinstatement.cnt, 0) AS reinstatement " +  // ← 추가!
        "FROM ( " +
        "    SELECT DISTINCT DATE(s.regist_date) AS date " +
        "    FROM student_mst s " +
        "    WHERE s.dojang_code = :dojangCode " +
        "    AND DATE_FORMAT(s.regist_date, '%Y%m') = :month " +
        "    UNION " +
        "    SELECT DISTINCT DATE(ss.change_date) AS date " +
        "    FROM student_status ss " +
        "    JOIN student_mst s ON ss.student_code = s.student_code " +
        "    WHERE s.dojang_code = :dojangCode " +
        "    AND ss.status_code IN ('퇴관', '휴관', '복관') " +  // ← 휴관/복관 추가!
        "    AND DATE_FORMAT(ss.change_date, '%Y%m') = :month " +
        ") dates " +
        "LEFT JOIN ( " +
        "    SELECT DATE(s.regist_date) AS date, COUNT(DISTINCT ss.student_code) AS cnt " +
        "    FROM student_status ss " +
        "    JOIN student_mst s ON ss.student_code = s.student_code " +
        "    WHERE s.dojang_code = :dojangCode " +
        "    AND ss.status_code = '재원' " +
        "    AND DATE(ss.change_date) = DATE(s.regist_date) " +
        "    AND DATE_FORMAT(s.regist_date, '%Y%m') = :month " +
        "    AND ss.created_at = ( " +
        "        SELECT MAX(created_at) " +
        "        FROM student_status " +
        "        WHERE student_code = s.student_code " +
        "        AND DATE(change_date) = DATE(s.regist_date) " +
        "        AND status_code IN ('재원', '체험') " +
        "    ) " +
        "    GROUP BY DATE(s.regist_date) " +
        ") enroll ON dates.date = enroll.date " +
        "LEFT JOIN ( " +
        "    SELECT DATE(ss.change_date) AS date, COUNT(DISTINCT ss.student_code) AS cnt " +
        "    FROM student_status ss " +
        "    JOIN student_mst s ON ss.student_code = s.student_code " +
        "    WHERE s.dojang_code = :dojangCode " +
        "    AND ss.status_code = '퇴관' " +
        "    AND DATE_FORMAT(ss.change_date, '%Y%m') = :month " +
        "    GROUP BY DATE(ss.change_date) " +
        ") withdraw ON dates.date = withdraw.date " +
        "LEFT JOIN ( " +
        "    SELECT DATE(s.regist_date) AS date, COUNT(DISTINCT ss.student_code) AS cnt " +
        "    FROM student_status ss " +
        "    JOIN student_mst s ON ss.student_code = s.student_code " +
        "    WHERE s.dojang_code = :dojangCode " +
        "    AND ss.status_code = '체험' " +
        "    AND DATE(ss.change_date) = DATE(s.regist_date) " +
        "    AND DATE_FORMAT(s.regist_date, '%Y%m') = :month " +
        "    AND ss.created_at = ( " +
        "        SELECT MAX(created_at) " +
        "        FROM student_status " +
        "        WHERE student_code = s.student_code " +
        "        AND DATE(change_date) = DATE(s.regist_date) " +
        "        AND status_code IN ('재원', '체험') " +
        "    ) " +
        "    GROUP BY DATE(s.regist_date) " +
        ") trial ON dates.date = trial.date " +
        "LEFT JOIN ( " +                                       // ← 추가! 휴관
        "    SELECT DATE(ss.change_date) AS date, COUNT(DISTINCT ss.student_code) AS cnt " +
        "    FROM student_status ss " +
        "    JOIN student_mst s ON ss.student_code = s.student_code " +
        "    WHERE s.dojang_code = :dojangCode " +
        "    AND ss.status_code = '휴관' " +
        "    AND DATE_FORMAT(ss.change_date, '%Y%m') = :month " +
        "    GROUP BY DATE(ss.change_date) " +
        ") suspension ON dates.date = suspension.date " +
        "LEFT JOIN ( " +                                       // ← 추가! 복관
        "    SELECT DATE(ss.change_date) AS date, COUNT(DISTINCT ss.student_code) AS cnt " +
        "    FROM student_status ss " +
        "    JOIN student_mst s ON ss.student_code = s.student_code " +
        "    WHERE s.dojang_code = :dojangCode " +
        "    AND ss.status_code = '복관' " +
        "    AND DATE_FORMAT(ss.change_date, '%Y%m') = :month " +
        "    GROUP BY DATE(ss.change_date) " +
        ") reinstatement ON dates.date = reinstatement.date " +
        "ORDER BY dates.date",
        nativeQuery = true)
    List<Object[]> getDailyStudentStats(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
 
    /**
     * 대시보드 - 주간별 신규 입관 수 (주 시작일 기준)
     * student_status에서 입관일 당일 '재원' 이력 확인
     * 같은 날짜에 재원/체험 여러 번 변경 시 가장 최신 상태만 카운트 (퇴관은 별도)
     * 결과: [주 시작일, 입관수]
     */
    @Query(value = 
        "SELECT " +
        "    DATE_SUB(DATE(s.regist_date), INTERVAL WEEKDAY(s.regist_date) DAY) AS week_start, " +
        "    COUNT(DISTINCT ss.student_code) AS cnt " +
        "FROM student_status ss " +
        "JOIN student_mst s ON ss.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND ss.status_code = '재원' " +
        "AND DATE(ss.change_date) = DATE(s.regist_date) " +
        "AND DATE_FORMAT(s.regist_date, '%Y%m') = :month " +
        "AND ss.created_at = ( " +
        "    SELECT MAX(created_at) " +
        "    FROM student_status " +
        "    WHERE student_code = s.student_code " +
        "    AND DATE(change_date) = DATE(s.regist_date) " +
        "    AND status_code IN ('재원', '체험') " +
        ") " +
        "GROUP BY week_start " +
        "ORDER BY week_start",
        nativeQuery = true)
    List<Object[]> getWeeklyEnrollment(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 대시보드 - 월별 체험생 수
     * student_status에서 입관일 당일 '체험' 이력 확인
     * 같은 날짜에 재원/체험 여러 번 변경 시 가장 최신 상태만 카운트 (퇴관은 별도)
     */
    @Query(value = 
        "SELECT COUNT(DISTINCT ss.student_code) " +
        "FROM student_status ss " +
        "JOIN student_mst s ON ss.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND ss.status_code = '체험' " +
        "AND DATE(ss.change_date) = DATE(s.regist_date) " +
        "AND DATE_FORMAT(s.regist_date, '%Y%m') = :month " +
        "AND ss.created_at = ( " +
        "    SELECT MAX(created_at) " +
        "    FROM student_status " +
        "    WHERE student_code = s.student_code " +
        "    AND DATE(change_date) = DATE(s.regist_date) " +
        "    AND status_code IN ('재원', '체험') " +
        ")",
        nativeQuery = true)
    int countMonthlyTrial(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 통계 - 월별 전체 제자 수 (재원 + 체험)
     * 해당 월 말일 기준 재원생 수
     * 해당 월까지 입관한 학생 중 해당 월까지 퇴관하지 않은 학생
     */
    @Query(value = 
        "SELECT COUNT(*) FROM student_mst s " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND DATE_FORMAT(s.regist_date, '%Y%m') <= :month " +
        "AND NOT EXISTS ( " +
        "    SELECT 1 FROM student_status ss " +
        "    WHERE ss.student_code = s.student_code " +
        "    AND ss.status_code = '퇴관' " +
        "    AND DATE_FORMAT(ss.change_date, '%Y%m') <= :month " +
        ")",
        nativeQuery = true)
    int countMonthlyStudentsByMonth(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 통계 - 월별 신규 입관생 수 (재원)
     * 해당 월에 입관한 학생 중 입관 시 상태가 '재원'인 학생
     * student_status 테이블에서 입관일과 동일한 날짜의 '재원' 이력 확인
     * 같은 날짜에 재원/체험 여러 번 변경 시 가장 최신 상태만 카운트 (퇴관은 별도)
     */
    @Query(value = 
        "SELECT COUNT(DISTINCT ss.student_code) " +
        "FROM student_status ss " +
        "JOIN student_mst s ON ss.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND DATE_FORMAT(s.regist_date, '%Y%m') = :month " +
        "AND ss.status_code = '재원' " +
        "AND DATE(ss.change_date) = DATE(s.regist_date) " +
        "AND ss.created_at = ( " +
        "    SELECT MAX(created_at) " +
        "    FROM student_status " +
        "    WHERE student_code = s.student_code " +
        "    AND DATE(change_date) = DATE(s.regist_date) " +
        "    AND status_code IN ('재원', '체험') " +
        ")",
        nativeQuery = true)
    int countMonthlyEnrolledByMonth(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 통계 - 월별 신규 체험생 수
     * 해당 월에 입관한 학생 중 입관 시 상태가 '체험'인 학생
     * student_status 테이블에서 입관일과 동일한 날짜의 '체험' 이력 확인
     * 같은 날짜에 재원/체험 여러 번 변경 시 가장 최신 상태만 카운트 (퇴관은 별도)
     */
    @Query(value = 
        "SELECT COUNT(DISTINCT ss.student_code) " +
        "FROM student_status ss " +
        "JOIN student_mst s ON ss.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND DATE_FORMAT(s.regist_date, '%Y%m') = :month " +
        "AND ss.status_code = '체험' " +
        "AND DATE(ss.change_date) = DATE(s.regist_date) " +
        "AND ss.created_at = ( " +
        "    SELECT MAX(created_at) " +
        "    FROM student_status " +
        "    WHERE student_code = s.student_code " +
        "    AND DATE(change_date) = DATE(s.regist_date) " +
        "    AND status_code IN ('재원', '체험') " +
        ")",
        nativeQuery = true)
    int countMonthlyTrialByMonth(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 통계 - 월별 퇴관생 수
     * 해당 월에 퇴관한 학생 수
     */
    @Query(value = 
        "SELECT COUNT(DISTINCT ss.student_code) " +
        "FROM student_status ss " +
        "JOIN student_mst s ON ss.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND ss.status_code = '퇴관' " +
        "AND DATE_FORMAT(ss.change_date, '%Y%m') = :month",
        nativeQuery = true)
    int countMonthlyWithdrawnByMonth(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);

    /**
     * 통계 - 월말 기준 휴관생 수 (해당 월에 휴관 이력이 생긴 인원)
     */
    @Query(value =
        "SELECT COUNT(DISTINCT ss.student_code) " +
        "FROM student_status ss " +
        "JOIN student_mst s ON ss.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND ss.status_code = '휴관' " +
        "AND DATE_FORMAT(ss.change_date, '%Y%m') = :month",
        nativeQuery = true)
    int countMonthlySuspendedByMonth(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
 
    /**
     * 통계 - 월말 기준 복관생 수 (해당 월에 복관 이력이 생긴 인원)
     */
    @Query(value =
        "SELECT COUNT(DISTINCT ss.student_code) " +
        "FROM student_status ss " +
        "JOIN student_mst s ON ss.student_code = s.student_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND ss.status_code = '복관' " +
        "AND DATE_FORMAT(ss.change_date, '%Y%m') = :month",
        nativeQuery = true)
    int countMonthlyReinstatedByMonth(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 제자관리결산 - 성별 재원생 수
     * 결과: [성별코드, 인원수]
     */
    @Query(value = 
        "SELECT gender_code, COUNT(*) " +
        "FROM student_mst " +
        "WHERE dojang_code = :dojangCode " +
        "AND status_code = '재원' " +
        "AND is_deleted = 0 " +
        "GROUP BY gender_code " +
        "ORDER BY gender_code",
        nativeQuery = true)
    List<Object[]> countByGender(@Param("dojangCode") String dojangCode);
    
    /**
     * 제자관리결산 - 연령대별 재원생 수
     * 결과: [연령대, 인원수]
     */
    @Query(value = 
            "SELECT " +
            "    CASE " +
            "        WHEN (YEAR(CURDATE()) - YEAR(birth_date) + 1) BETWEEN 1 AND 7 THEN '유아부' " +
            "        WHEN (YEAR(CURDATE()) - YEAR(birth_date) + 1) BETWEEN 8 AND 13 THEN '초등부' " +
            "        WHEN (YEAR(CURDATE()) - YEAR(birth_date) + 1) BETWEEN 14 AND 16 THEN '중등부' " +
            "        WHEN (YEAR(CURDATE()) - YEAR(birth_date) + 1) BETWEEN 17 AND 19 THEN '고등부' " +
            "        ELSE '성인부' " +
            "    END AS age_group, " +
            "    COUNT(*) AS student_count " +
            "FROM student_mst " +
            "WHERE dojang_code = :dojangCode " +
            "AND status_code IN ('재원', '복관') " +
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
        List<Object[]> countByAgeGroup(@Param("dojangCode") String dojangCode);
    
    /**
     * 알림 - 오늘 생일인 제자 조회
     */
    @Query(value = 
        "SELECT * FROM student_mst " +
        "WHERE MONTH(birth_date) = :month " +
        "AND DAY(birth_date) = :day " +
        "AND status_code IN ('재원', '체험')",
        nativeQuery = true)
    List<Student> findByBirthMonthAndDay(
        @Param("month") int month,
        @Param("day") int day);
    
    /**
     * 앱 제자 목록 조회 (동적 검색 + 페이징)
     * 결과: [제자코드, 제자명, 성별코드, 생년월일, 학년, 급수명, 연락처, 재원상태코드, 입관일, 퇴관일]
     */
    @Query(value =
            "SELECT " +
            "    s.student_code, " +
            "    s.student_name, " +
            "    s.gender_code, " +
            "    s.birth_date, " +
            "    s.grade, " +
            "    c.code_name AS belt_name, " +
            "    s.student_phone, " +
            "    s.status_code, " +
            "    s.regist_date, " +
            "    s.deleted_at, " +
            "    s.rope_belt_code, " +
            "    rc.code_name AS rope_belt_name " +
            "FROM student_mst s " +
            "LEFT JOIN common_mst c ON s.belt_code = c.common_code " +
            "LEFT JOIN common_mst rc ON s.rope_belt_code = rc.common_code " +
            "WHERE s.dojang_code = :dojangCode " +
            "AND s.is_deleted = 0 " +
            "AND (:studentSearch = '' OR s.student_name LIKE CONCAT('%', :studentSearch, '%') " +
            "     OR s.student_code LIKE CONCAT('%', :studentSearch, '%')) " +
            "AND (:grade = '전체' OR s.grade = :grade) " +
            "AND (:genderCode = '전체' OR s.gender_code = :genderCode) " +
            "AND (:beltCode = '전체' OR s.belt_code = :beltCode OR s.rope_belt_code = :beltCode) " +
            "AND (:statusCode = '전체' OR s.status_code = :statusCode) " +
            "ORDER BY s.student_name ASC",
            countQuery =
            "SELECT COUNT(*) FROM student_mst s " +
            "WHERE s.dojang_code = :dojangCode " +
            "AND s.is_deleted = 0 " +
            "AND (:studentSearch = '' OR s.student_name LIKE CONCAT('%', :studentSearch, '%') " +
            "     OR s.student_code LIKE CONCAT('%', :studentSearch, '%')) " +
            "AND (:grade = '전체' OR s.grade = :grade) " +
            "AND (:genderCode = '전체' OR s.gender_code = :genderCode) " +
            "AND (:beltCode = '전체' OR s.belt_code = :beltCode OR s.rope_belt_code = :beltCode) " +
            "AND (:statusCode = '전체' OR s.status_code = :statusCode)",
            nativeQuery = true)
        Page<Object[]> findStudentsForApp(
                @Param("dojangCode") String dojangCode,
                @Param("studentSearch") String studentSearch,
                @Param("grade") String grade,
                @Param("genderCode") String genderCode,
                @Param("beltCode") String beltCode,
                @Param("statusCode") String statusCode,
                Pageable pageable);

    /**
     * 재원 + 체험 제자 수 조회
     */
    @Query(value = """
        SELECT COUNT(*) 
        FROM student_mst 
        WHERE status_code IN (:statuses) AND is_deleted = :isDeleted
        """, nativeQuery = true)
    long countByStudentStatusInAndIsDeleted(
            @Param("statuses") List<String> statuses, 
            @Param("isDeleted") Integer isDeleted);
 
    /**
     * 도장별 재원 제자 수 조회
     */
    @Query(value = """
        SELECT s.dojang_code, COUNT(*) as cnt
        FROM student_mst s
        WHERE s.status_code = '재원' AND s.is_deleted = 0
        GROUP BY s.dojang_code
        """, nativeQuery = true)
    List<Object[]> countEnrolledByDojang();
 
    /**
     * 제자수 TOP 5 도장 조회
     */
    @Query(value = """
        SELECT d.dojang_name, COUNT(s.student_code) as student_count
        FROM taekwondo_mst d
        LEFT JOIN student_mst s ON d.dojang_code = s.dojang_code
        WHERE s.status_code IN ('재원', '체험') AND s.is_deleted = 0
        GROUP BY d.dojang_code, d.dojang_name
        ORDER BY student_count DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> findTop5ByStudentCount();
 
    /**
     * 재원현황 TOP 3 조회 (휴관/복관 추가, LIMIT 5 → 3)
     */
    @Query(value = """
        SELECT 
          d.dojang_name,
          SUM(CASE WHEN s.status_code = '재원' THEN 1 ELSE 0 END) as enrolled,
          SUM(CASE WHEN s.status_code = '퇴관' THEN 1 ELSE 0 END) as withdrawn,
          SUM(CASE WHEN s.status_code = '체험' THEN 1 ELSE 0 END) as trial,
          SUM(CASE WHEN s.status_code = '휴관' THEN 1 ELSE 0 END) as suspended,
          SUM(CASE WHEN s.status_code = '복관' THEN 1 ELSE 0 END) as reinstated
        FROM taekwondo_mst d
        LEFT JOIN student_mst s ON d.dojang_code = s.dojang_code
        WHERE s.is_deleted = 0
        GROUP BY d.dojang_code, d.dojang_name
        ORDER BY enrolled DESC
        LIMIT 3
        """, nativeQuery = true)
    List<Object[]> findTop3ByEnrollmentStatus();
 
    /**
     * 특정 도장의 특정 상태별 제자 수 조회
     */
    @Query(value = """
        SELECT COUNT(*) 
        FROM student_mst 
        WHERE dojang_code = :dojangCode 
          AND status_code = :studentStatus 
          AND is_deleted = :isDeleted
        """, nativeQuery = true)
    long countByDojangCodeAndStudentStatusAndIsDeleted(
            @Param("dojangCode") String dojangCode, 
            @Param("studentStatus") String studentStatus, 
            @Param("isDeleted") Integer isDeleted);
 
    /**
     * 특정 도장의 제자 수 조회 (여러 상태)
     */
    @Query(value = """
        SELECT COUNT(*) 
        FROM student_mst 
        WHERE dojang_code = :dojangCode 
          AND status_code IN (:statuses) 
          AND is_deleted = :isDeleted
        """, nativeQuery = true)
    long countByDojangCodeAndStudentStatusInAndIsDeleted(
            @Param("dojangCode") String dojangCode, 
            @Param("statuses") List<String> statuses, 
            @Param("isDeleted") Integer isDeleted);
 
    /**
     * 학년별 제자수 조회를 위한 생년월일 목록
     */
    @Query(value = """
        SELECT s.birth_date
        FROM student_mst s
        WHERE s.dojang_code = :dojangCode
          AND s.status_code IN ('재원', '복관')
          AND s.is_deleted = 0
        """, nativeQuery = true)
    List<Object> findBirthDatesByDojang(@Param("dojangCode") String dojangCode);
    
    /**
     * 도장 + 상태코드로 제자 수 카운트
     */
    @Query(value = 
        "SELECT COUNT(*) FROM student_mst " +
        "WHERE dojang_code = :dojangCode " +
        "AND status_code = :statusCode",
        nativeQuery = true)
    int countByDojangCodeAndStatusCode(
        @Param("dojangCode") String dojangCode,
        @Param("statusCode") String statusCode);
    
    /**
     * 상태코드로 제자 수 카운트
     */
    @Query(value = 
        "SELECT COUNT(*) FROM student_mst " +
        "WHERE status_code = :statusCode",
        nativeQuery = true)
    int countByStatusCode(@Param("statusCode") String statusCode);
    
    /**
     * 특정 날짜 입관/퇴관/체험 제자 목록 (팝업용)
     * (student_code, student_name, gender_code, birth_date, belt_code, belt_name, rope_belt_code, rope_belt_name)
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
        "FROM student_mst s " +
        "JOIN student_status ss ON s.student_code = ss.student_code " +
        "LEFT JOIN common_mst c ON s.belt_code = c.common_code " +
        "LEFT JOIN common_mst rc ON s.rope_belt_code = rc.common_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND ss.change_date = :date " +
        "AND ss.status_code = :statusCode " +
        "ORDER BY s.student_name",
        nativeQuery = true)
    List<Object[]> getStudentsByDateAndStatus(
            @Param("dojangCode") String dojangCode,
            @Param("date") LocalDate date,
            @Param("statusCode") String statusCode);

    
    
}