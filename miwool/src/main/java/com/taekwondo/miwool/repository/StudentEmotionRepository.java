package com.taekwondo.miwool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.taekwondo.miwool.entity.StudentEmotion;

import java.util.List;

@Repository 
public interface StudentEmotionRepository extends JpaRepository<StudentEmotion, Integer> { 
    List<StudentEmotion> findByStudentCode(String studentCode);
    
    void deleteByStudentCode(String studentCode);
    
    @Query(value = 
            "SELECT se.emotion_code, c.code_name, COUNT(DISTINCT se.student_code) " +
            "FROM student_emotion se " +
            "INNER JOIN student_mst s ON se.student_code = s.student_code " +
            "INNER JOIN common_mst c ON se.emotion_code = c.common_code " +
            "WHERE s.dojang_code = :dojangCode AND s.status_code IN ('재원', '복관') AND c.group_code = 'PERS_EMOT' " +
            "GROUP BY se.emotion_code, c.code_name ORDER BY c.code_order",
            nativeQuery = true)
        List<Object[]> getEmotionStats(@Param("dojangCode") String dojangCode);
        
    @Query(value = 
            "SELECT c.code_name " +
            "FROM student_emotion se " +
            "INNER JOIN common_mst c ON se.emotion_code = c.common_code " +
            "WHERE se.student_code = :studentCode " +
            "ORDER BY c.code_order",
            nativeQuery = true)
        List<String> findEmotionNamesByStudent(@Param("studentCode") String studentCode);
        
        
}
