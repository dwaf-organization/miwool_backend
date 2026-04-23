package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentGuardian;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentGuardianRepository extends JpaRepository<StudentGuardian, Integer> {
	
    /**
     * 제자코드로 보호자 관계 조회
     */
    List<StudentGuardian> findByStudentCode(String studentCode);
    
    // 제자-보호자 매핑 조회 (수정/삭제 시 사용)
    Optional<StudentGuardian> findByStudentCodeAndGuardianCode(String studentCode, String guardianCode);
    
    // 보호자를 참조하는 제자 수 조회 (삭제 시 사용)
    long countByGuardianCode(String guardianCode);
    
    /**
     * 보호자코드로 연결된 제자 관계 조회
     */
    List<StudentGuardian> findByGuardianCode(String guardianCode);
    
}