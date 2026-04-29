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
    	    "    s.deleted_at " +
    	    "FROM student_mst s " +
    	    "WHERE s.dojang_code = :dojangCode " +
    	    "AND (:studentSearch IS NULL OR s.student_name LIKE CONCAT('%', :studentSearch, '%') OR s.student_code LIKE CONCAT('%', :studentSearch, '%')) " +
    	    "AND (:genderCode IS NULL OR s.gender_code = :genderCode) " +
    	    "AND (:beltCode IS NULL OR s.belt_code = :beltCode) " +
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
    	    "AND (:beltCode IS NULL OR s.belt_code = :beltCode) " +
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
                   "    c.code_name AS belt_name " +
                   "FROM student_mst s " +
                   "LEFT JOIN common_mst c ON s.belt_code = c.common_code " +
                   "WHERE s.dojang_code = :dojangCode " +
                   "  AND s.status_code = '재원'  " +
                   "  AND (:studentSearch IS NULL " +
                   "       OR s.student_code LIKE CONCAT('%', :studentSearch, '%') " +
                   "       OR s.student_name LIKE CONCAT('%', :studentSearch, '%')) " +
                   "  AND (:beltCode IS NULL OR s.belt_code = :beltCode) " +
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
     */
    @Query(value = 
        "SELECT COUNT(*) FROM student_mst " +
        "WHERE dojang_code = :dojangCode " +
        "AND DATE_FORMAT(regist_date, '%Y%m') = :month",
        nativeQuery = true)
    int countMonthlyEnrollment(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 대시보드 - 월별 퇴관생 수
     */
    @Query(value = 
        "SELECT COUNT(*) FROM student_mst " +
        "WHERE dojang_code = :dojangCode " +
        "AND status_code = '퇴관' " +
        "AND DATE_FORMAT(deleted_at, '%Y%m') = :month",
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
        "AND status_code IN ('재원', '체험')",
        nativeQuery = true)
    int countCurrentTotal(@Param("dojangCode") String dojangCode);
    
    /**
     * 대시보드 - 일별 입관/퇴관/체험 수 조회
     * 결과: [날짜, 입관수, 퇴관수, 체험수]
     */
    @Query(value = 
        "SELECT " +
        "    dates.date, " +
        "    COALESCE(enroll.cnt, 0) AS enrollment, " +
        "    COALESCE(withdraw.cnt, 0) AS withdrawal, " +
        "    COALESCE(trial.cnt, 0) AS trial " +
        "FROM ( " +
        "    SELECT DATE(regist_date) AS date FROM student_mst " +
        "    WHERE dojang_code = :dojangCode AND DATE_FORMAT(regist_date, '%Y%m') = :month " +
        "    UNION " +
        "    SELECT DATE(deleted_at) AS date FROM student_mst " +
        "    WHERE dojang_code = :dojangCode AND status_code = '퇴관' AND DATE_FORMAT(deleted_at, '%Y%m') = :month " +
        ") dates " +
        "LEFT JOIN ( " +
        "    SELECT DATE(regist_date) AS date, COUNT(*) AS cnt " +
        "    FROM student_mst " +
        "    WHERE dojang_code = :dojangCode AND status_code = '재원' AND DATE_FORMAT(regist_date, '%Y%m') = :month " +
        "    GROUP BY DATE(regist_date) " +
        ") enroll ON dates.date = enroll.date " +
        "LEFT JOIN ( " +
        "    SELECT DATE(deleted_at) AS date, COUNT(*) AS cnt " +
        "    FROM student_mst " +
        "    WHERE dojang_code = :dojangCode AND status_code = '퇴관' AND DATE_FORMAT(deleted_at, '%Y%m') = :month " +
        "    GROUP BY DATE(deleted_at) " +
        ") withdraw ON dates.date = withdraw.date " +
        "LEFT JOIN ( " +
        "    SELECT DATE(regist_date) AS date, COUNT(*) AS cnt " +
        "    FROM student_mst " +
        "    WHERE dojang_code = :dojangCode AND status_code = '체험' AND DATE_FORMAT(regist_date, '%Y%m') = :month " +
        "    GROUP BY DATE(regist_date) " +
        ") trial ON dates.date = trial.date " +
        "ORDER BY dates.date ASC",
        nativeQuery = true)
    List<Object[]> getDailyStudentStats(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);

    /**
     * 대시보드 - 주차별 신규 입관 수
     * 일요일 시작 기준, status_code = '재원'
     * 결과: [주_시작일, 입관수]
     */
    @Query(value = 
        "SELECT " +
        "    DATE_SUB(regist_date, INTERVAL (DAYOFWEEK(regist_date) - 1) DAY) AS week_start, " +
        "    COUNT(*) AS enrollment_count " +
        "FROM student_mst " +
        "WHERE dojang_code = :dojangCode " +
        "AND status_code = '재원' " +
        "AND DATE_FORMAT(regist_date, '%Y%m') = :month " +
        "GROUP BY week_start " +
        "ORDER BY week_start ASC",
        nativeQuery = true)
    List<Object[]> getWeeklyEnrollment(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 대시보드 - 월별 체험생 수
     */
    @Query(value = 
        "SELECT COUNT(*) FROM student_mst " +
        "WHERE dojang_code = :dojangCode " +
        "AND status_code = '체험' " +
        "AND DATE_FORMAT(regist_date, '%Y%m') = :month",
        nativeQuery = true)
    int countMonthlyTrial(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 통계 - 월별 입관생 수 (재원)
     */
    @Query(value = 
        "SELECT COUNT(*) FROM student_mst " +
        "WHERE dojang_code = :dojangCode " +
        "AND status_code = '재원' " +
        "AND DATE_FORMAT(regist_date, '%Y%m') = :month",
        nativeQuery = true)
    int countMonthlyEnrolledByMonth(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 통계 - 월별 체험생 수
     */
    @Query(value = 
        "SELECT COUNT(*) FROM student_mst " +
        "WHERE dojang_code = :dojangCode " +
        "AND status_code = '체험' " +
        "AND DATE_FORMAT(regist_date, '%Y%m') = :month",
        nativeQuery = true)
    int countMonthlyTrialByMonth(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    /**
     * 통계 - 월별 퇴관생 수
     */
    @Query(value = 
        "SELECT COUNT(*) FROM student_mst " +
        "WHERE dojang_code = :dojangCode " +
        "AND status_code = '퇴관' " +
        "AND DATE_FORMAT(deleted_at, '%Y%m') = :month",
        nativeQuery = true)
    int countMonthlyWithdrawnByMonth(
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
        "        WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 0 AND 7 THEN '유아' " +
        "        WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 8 AND 13 THEN '초등부' " +
        "        WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 14 AND 16 THEN '중등부' " +
        "        WHEN TIMESTAMPDIFF(YEAR, birth_date, CURDATE()) BETWEEN 17 AND 19 THEN '고등부' " +
        "        ELSE '성인부' " +
        "    END AS age_group, " +
        "    COUNT(*) AS student_count " +
        "FROM student_mst " +
        "WHERE dojang_code = :dojangCode " +
        "AND status_code = '재원' " +
        "GROUP BY age_group " +
        "ORDER BY " +
        "    CASE age_group " +
        "        WHEN '유아' THEN 1 " +
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
        "    s.deleted_at " +
        "FROM student_mst s " +
        "LEFT JOIN common_mst c ON s.belt_code = c.common_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND (:studentSearch IS NULL OR :studentSearch = '' OR " +
        "     s.student_name LIKE CONCAT('%', :studentSearch, '%') OR " +
        "     s.student_code LIKE CONCAT('%', :studentSearch, '%')) " +
        "AND (:grade = '전체' OR s.grade = :grade) " +
        "AND (:genderCode = '전체' OR s.gender_code = :genderCode) " +
        "AND (:beltCode = '전체' OR s.belt_code = :beltCode) " +
        "AND (:statusCode = '전체' OR s.status_code = :statusCode) " +
        "ORDER BY s.student_name ASC",
        countQuery = 
        "SELECT COUNT(*) " +
        "FROM student_mst s " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND (:studentSearch IS NULL OR :studentSearch = '' OR " +
        "     s.student_name LIKE CONCAT('%', :studentSearch, '%') OR " +
        "     s.student_code LIKE CONCAT('%', :studentSearch, '%')) " +
        "AND (:grade = '전체' OR s.grade = :grade) " +
        "AND (:genderCode = '전체' OR s.gender_code = :genderCode) " +
        "AND (:beltCode = '전체' OR s.belt_code = :beltCode) " +
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

    
}