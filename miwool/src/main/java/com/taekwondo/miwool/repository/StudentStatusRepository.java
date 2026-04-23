package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentStatusRepository extends JpaRepository<StudentStatus, String> {
    
	Optional<StudentStatus> findFirstByStatusHistoryCodeStartingWithOrderByStatusHistoryCodeDesc(String prefix);
	
    /**
     * 해당 월에 생성된 상태 이력 수 조회 (statusHistoryCode 생성용)
     */
    @Query("SELECT COUNT(s) FROM StudentStatus s WHERE s.statusHistoryCode LIKE :prefix")
    long countByStatusHistoryCodePrefix(@Param("prefix") String prefix);
    
    /**
     * 제자별 최신 재원상태 조회 (생성일 기준)
     */
    Optional<StudentStatus> findTopByStudentCodeOrderByCreatedAtDesc(String studentCode);
}