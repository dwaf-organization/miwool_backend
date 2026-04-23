package com.taekwondo.miwool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taekwondo.miwool.entity.StudentEducation;

import java.util.List;

@Repository
public interface StudentEducationRepository extends JpaRepository<StudentEducation, Integer> {
    List<StudentEducation> findByStudentCode(String studentCode);
    
    // 제자의 부모교육가치 삭제 (저장 시 사용)
    void deleteByStudentCode(String studentCode);
    
    @Query(value = 
            "SELECT se.emotion_code, c.code_name, COUNT(DISTINCT se.student_code) " +
            "FROM student_emotion se " +
            "INNER JOIN student_mst s ON se.student_code = s.student_code " +
            "INNER JOIN common_mst c ON se.emotion_code = c.common_code " +
            "WHERE s.dojang_code = :dojangCode AND s.status_code = '재원' AND c.group_code = 'PERS_EMOT' " +
            "GROUP BY se.emotion_code, c.code_name ORDER BY c.code_order",
            nativeQuery = true)
        List<Object[]> getEmotionStats(@Param("dojangCode") String dojangCode);
        
}