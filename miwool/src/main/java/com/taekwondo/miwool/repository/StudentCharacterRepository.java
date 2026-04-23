package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentCharacter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository 
public interface StudentCharacterRepository extends JpaRepository<StudentCharacter, Integer> { 
    List<StudentCharacter> findByStudentCode(String studentCode);
    
    void deleteByStudentCode(String studentCode);
    
    /**
     * 통계 - 기본성향별 인원수 (재원생만)
     * 결과: [성향코드, 성향명, 인원수]
     */
    @Query(value = 
        "SELECT " +
        "    sc.character_code, " +
        "    c.code_name, " +
        "    COUNT(DISTINCT sc.student_code) AS student_count " +
        "FROM student_character sc " +
        "INNER JOIN student_mst s ON sc.student_code = s.student_code " +
        "INNER JOIN common_mst c ON sc.character_code = c.common_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND s.status_code = '재원' " +
        "AND c.group_code = 'PERS_BASIC' " +
        "GROUP BY sc.character_code, c.code_name " +
        "ORDER BY c.code_order",
        nativeQuery = true)
    List<Object[]> getCharacterStats(@Param("dojangCode") String dojangCode);
    
    /**
     * 앱 제자 상세 - 기본성향 조회
     */
    @Query(value = 
        "SELECT c.code_name " +
        "FROM student_character sc " +
        "INNER JOIN common_mst c ON sc.character_code = c.common_code " +
        "WHERE sc.student_code = :studentCode " +
        "ORDER BY c.code_order",
        nativeQuery = true)
    List<String> findCharacterNamesByStudent(@Param("studentCode") String studentCode);
    

}
