package com.taekwondo.miwool.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.taekwondo.miwool.entity.StudentSocial;

import java.util.List;

@Repository 
public interface StudentSocialRepository extends JpaRepository<StudentSocial, Integer> { 
    List<StudentSocial> findByStudentCode(String studentCode);
    
    void deleteByStudentCode(String studentCode);
    
    @Query(value = 
            "SELECT ss.social_code, c.code_name, COUNT(DISTINCT ss.student_code) " +
            "FROM student_social ss " +
            "INNER JOIN student_mst s ON ss.student_code = s.student_code " +
            "INNER JOIN common_mst c ON ss.social_code = c.common_code " +
            "WHERE s.dojang_code = :dojangCode AND s.status_code IN ('재원', '복관') AND c.group_code = 'PERS_SOC' " +
            "GROUP BY ss.social_code, c.code_name ORDER BY c.code_order",
            nativeQuery = true)
    List<Object[]> getSocialStats(@Param("dojangCode") String dojangCode);

        
    @Query(value = 
            "SELECT c.code_name " +
            "FROM student_social ss " +
            "INNER JOIN common_mst c ON ss.social_code = c.common_code " +
            "WHERE ss.student_code = :studentCode " +
            "ORDER BY c.code_order",
            nativeQuery = true)
    List<String> findSocialNamesByStudent(@Param("studentCode") String studentCode);
    
    
}