package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentStatusRepository extends JpaRepository<StudentStatus, String> {
    
	Optional<StudentStatus> findFirstByStatusHistoryCodeStartingWithOrderByStatusHistoryCodeDesc(String prefix);
	
    // 재원이력 전체 조회 (날짜 내림차순)
    List<StudentStatus> findByStudentCodeOrderByChangeDateDescCreatedAtDesc(String studentCode);
    
    /**
     * 해당 월에 생성된 상태 이력 수 조회 (statusHistoryCode 생성용)
     */
    @Query("SELECT COUNT(s) FROM StudentStatus s WHERE s.statusHistoryCode LIKE :prefix")
    long countByStatusHistoryCodePrefix(@Param("prefix") String prefix);
    
    /**
     * 제자별 최신 재원상태 조회 (생성일 기준)
     */
    Optional<StudentStatus> findTopByStudentCodeOrderByCreatedAtDesc(String studentCode);
    
    // countByStatusAsOfDate - 복관을 재원으로 집계 (기존 메서드 수정)
    @Query(value = """
        SELECT 
          ss.status_code,
          COUNT(DISTINCT ss.student_code) as count
        FROM (
          SELECT 
            s1.student_code,
            s1.status_code
          FROM student_status s1
          INNER JOIN (
            SELECT 
              student_code,
              MAX(change_date) as max_date
            FROM student_status
            WHERE change_date <= :endOfMonth
            GROUP BY student_code
          ) s2 ON s1.student_code = s2.student_code 
              AND s1.change_date = s2.max_date
          INNER JOIN student_mst sm ON s1.student_code = sm.student_code
          WHERE sm.dojang_code = :dojangCode
            AND sm.is_deleted = 0
            AND sm.created_at <= :endOfMonth
        ) ss
        GROUP BY ss.status_code
        """, nativeQuery = true)
    List<Object[]> countByStatusAsOfDate(
            @Param("dojangCode") String dojangCode,
            @Param("endOfMonth") LocalDate endOfMonth);
    
    /**
     * 퇴관 학생들의 최근 퇴관일 조회
     */
    @Query(value = """
        SELECT 
            student_code,
            MAX(change_date) as withdrawal_date
        FROM student_status
        WHERE student_code IN (:studentCodes)
          AND status_code = '퇴관'
        GROUP BY student_code
        """, nativeQuery = true)
    List<Object[]> findWithdrawalDatesByStudentCodes(@Param("studentCodes") List<String> studentCodes);
    
    
}