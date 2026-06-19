package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSkillRepository extends JpaRepository<StudentSkill, Integer> {

    // 제자코드로 단일 조회 (단일 선택)
    Optional<StudentSkill> findByStudentCode(String studentCode);

    // 제자코드로 삭제 (저장 전 기존 데이터 제거)
    @Modifying
    @Query("DELETE FROM StudentSkill ss WHERE ss.studentCode = :studentCode")
    void deleteByStudentCode(@Param("studentCode") String studentCode);
    
    /**
     * 통계 - 기능습득속도별 인원수 (재원생만)
     * 결과: [skill_code, code_name, student_count]
     */
    @Query(value =
        "SELECT " +
        "    ss.skill_code, " +
        "    c.code_name, " +
        "    COUNT(DISTINCT ss.student_code) AS student_count " +
        "FROM student_skill ss " +
        "INNER JOIN student_mst s ON ss.student_code = s.student_code " +
        "INNER JOIN common_mst c ON ss.skill_code = c.common_code " +
        "WHERE s.dojang_code = :dojangCode " +
        "AND s.status_code IN ('재원', '복관') " +
        "AND c.group_code = 'SKILL' " +
        "GROUP BY ss.skill_code, c.code_name " +
        "ORDER BY c.code_order",
        nativeQuery = true)
    List<Object[]> getSkillStats(@Param("dojangCode") String dojangCode);
    
}