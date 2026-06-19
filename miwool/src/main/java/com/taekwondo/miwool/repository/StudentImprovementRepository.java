package com.taekwondo.miwool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.taekwondo.miwool.entity.StudentImprovement;

import java.util.List;

@Repository
public interface StudentImprovementRepository extends JpaRepository<StudentImprovement, Integer> {
    List<StudentImprovement> findByStudentCode(String studentCode);
    
    void deleteByStudentCode(String studentCode);
    
    @Query(value = 
            "SELECT si.improvement_code, c.code_name, COUNT(DISTINCT si.student_code) " +
            "FROM student_improvement si " +
            "INNER JOIN student_mst s ON si.student_code = s.student_code " +
            "INNER JOIN common_mst c ON si.improvement_code = c.common_code " +
            "WHERE s.dojang_code = :dojangCode AND s.status_code IN ('재원', '복관') AND c.group_code = 'CHANGE_NEED' " +
            "GROUP BY si.improvement_code, c.code_name ORDER BY c.code_order",
            nativeQuery = true)
    List<Object[]> getImprovementStats(@Param("dojangCode") String dojangCode);
        
    @Query(value = 
            "SELECT c.code_name " +
            "FROM student_improvement si " +
            "INNER JOIN common_mst c ON si.improvement_code = c.common_code " +
            "WHERE si.student_code = :studentCode " +
            "ORDER BY c.code_order",
            nativeQuery = true)
    List<String> findImprovementNamesByStudent(@Param("studentCode") String studentCode);
    
    
}