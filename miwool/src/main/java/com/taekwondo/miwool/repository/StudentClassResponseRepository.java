package com.taekwondo.miwool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.taekwondo.miwool.entity.StudentClassResponse;

import java.util.List;

@Repository 
public interface StudentClassResponseRepository extends JpaRepository<StudentClassResponse, Integer> { 
    List<StudentClassResponse> findByStudentCode(String studentCode);
    
    void deleteByStudentCode(String studentCode);
    
    @Query(value = 
            "SELECT scr.class_response_code, c.code_name, COUNT(DISTINCT scr.student_code) " +
            "FROM student_class_response scr " +
            "INNER JOIN student_mst s ON scr.student_code = s.student_code " +
            "INNER JOIN common_mst c ON scr.class_response_code = c.common_code " +
            "WHERE s.dojang_code = :dojangCode AND s.status_code IN ('재원', '복관') AND c.group_code = 'PERS_LESSON' " +
            "GROUP BY scr.class_response_code, c.code_name ORDER BY c.code_order",
            nativeQuery = true)
    List<Object[]> getClassResponseStats(@Param("dojangCode") String dojangCode);
        
    @Query(value = 
            "SELECT c.code_name " +
            "FROM student_class_response scr " +
            "INNER JOIN common_mst c ON scr.class_response_code = c.common_code " +
            "WHERE scr.student_code = :studentCode " +
            "ORDER BY c.code_order",
            nativeQuery = true)
    List<String> findClassResponseNamesByStudent(@Param("studentCode") String studentCode);
        
        
}
