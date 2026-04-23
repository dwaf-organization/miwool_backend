package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentPurpose;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentPurposeRepository extends JpaRepository<StudentPurpose, Long> {
	
    /**
     * 제자코드로 등록목적 조회
     */
    List<StudentPurpose> findByStudentCode(String studentCode);
    
    /**
     * 통계 - 등록목적별 인원수 (재원생만)
     * 결과: [목적코드, 목적명, 인원수]
     */
    @Query(value = 
        "SELECT " +
        "    sp.purpose_code, " +
        "    c.code_name, " +
        "    COUNT(DISTINCT sp.student_code) AS student_count " +
        "FROM student_purpose sp " +
        "INNER JOIN student_mst s ON sp.student_code = s.student_code " +
        "INNER JOIN common_mst c ON sp.purpose_code = c.common_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND s.status_code = '재원' " +
        "AND c.group_code = 'PURPOSE' " +
        "GROUP BY sp.purpose_code, c.code_name " +
        "ORDER BY c.code_order",
        nativeQuery = true)
    List<Object[]> getPurposeStats(@Param("dojangCode") String dojangCode);
    
}