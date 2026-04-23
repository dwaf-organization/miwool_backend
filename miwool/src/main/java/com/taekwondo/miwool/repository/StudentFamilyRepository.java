package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentFamily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentFamilyRepository extends JpaRepository<StudentFamily, Integer> {
    
    /**
     * 특정 제자의 가족 리스트 조회
     */
    List<StudentFamily> findByStudentCode(String studentCode);
    
    // 제자의 가족 정보 조회
    Optional<StudentFamily> findFamilyInfoByStudentCode(String studentCode);
    
    
}