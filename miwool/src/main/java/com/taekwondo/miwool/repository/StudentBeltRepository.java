package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentBelt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentBeltRepository extends JpaRepository<StudentBelt, String> {
    
	Optional<StudentBelt> findFirstByBeltHistoryCodeStartingWithOrderByBeltHistoryCodeDesc(String prefix);
	
    /**
     * 해당 월에 생성된 급수 이력 수 조회 (beltHistoryCode 생성용)
     */
    @Query("SELECT COUNT(b) FROM StudentBelt b WHERE b.beltHistoryCode LIKE :prefix")
    long countByBeltHistoryCodePrefix(@Param("prefix") String prefix);
    
    /**
     * 제자별 최신 급수 조회 (생성일 기준)
     */
    Optional<StudentBelt> findTopByStudentCodeOrderByCreatedAtDesc(String studentCode);
    
    /**
     * 제자코드로 급수 이력 조회
     */
    List<StudentBelt> findByStudentCode(String studentCode);
    
    /**
     * 제자의 급수/경력 이력 목록 조회 (최신순)
     */
    List<StudentBelt> findByStudentCodeOrderByAcquiredAtDesc(String studentCode);

    
}