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
    
    
}