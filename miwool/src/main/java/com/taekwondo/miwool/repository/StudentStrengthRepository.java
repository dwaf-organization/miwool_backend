package com.taekwondo.miwool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.taekwondo.miwool.entity.StudentStrength;

import java.util.List;

@Repository
public interface StudentStrengthRepository extends JpaRepository<StudentStrength, Integer> {
    List<StudentStrength> findByStudentCode(String studentCode);
    
    void deleteByStudentCode(String studentCode);
    
    @Query(value = 
            "SELECT ss.strength_code, c.code_name, COUNT(DISTINCT ss.student_code) " +
            "FROM student_strength ss " +
            "INNER JOIN student_mst s ON ss.student_code = s.student_code " +
            "INNER JOIN common_mst c ON ss.strength_code = c.common_code " +
            "WHERE s.dojang_code = :dojangCode AND s.status_code IN ('재원', '복관') AND c.group_code = 'STRENGTH' " +
            "GROUP BY ss.strength_code, c.code_name ORDER BY c.code_order",
            nativeQuery = true)
    List<Object[]> getStrengthStats(@Param("dojangCode") String dojangCode);
        
    @Query(value = 
            "SELECT c.code_name " +
            "FROM student_strength ss " +
            "INNER JOIN common_mst c ON ss.strength_code = c.common_code " +
            "WHERE ss.student_code = :studentCode " +
            "ORDER BY c.code_order",
            nativeQuery = true)
    List<String> findStrengthNamesByStudent(@Param("studentCode") String studentCode);
        
        
}