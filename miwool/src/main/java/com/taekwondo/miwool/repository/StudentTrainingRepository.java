package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentTraining;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentTrainingRepository extends JpaRepository<StudentTraining, Integer> {
    
    /**
     * 제자의 모든 수련정보 삭제
     * 퇴관 처리 시 사용
     */
    @Modifying
    @Query("DELETE FROM StudentTraining st WHERE st.studentCode = :studentCode")
    int deleteByStudentCode(@Param("studentCode") String studentCode);
    
    /**
     * 학생별 수련정보 목록 조회
     */
    List<StudentTraining> findByStudentCode(String studentCode);
    
    /**
     * 수련정보코드로 조회
     */
    Optional<StudentTraining> findById(Integer trainingInfoCode);
    
    // 휴관 처리 시 현재 수련정보 end_date 업데이트
    @Modifying
    @Query("UPDATE StudentTraining st SET st.endDate = :endDate " +
           "WHERE st.studentCode = :studentCode AND st.isCurrent = 1")
    int updateEndDateByStudentCode(
            @Param("studentCode") String studentCode,
            @Param("endDate") LocalDate endDate);
 
    // 스케줄러 - 활성 수련정보 조회 (퇴관 + 휴관 제외) - 기존 메서드 수정
    @Query(value =
        "SELECT st.* FROM student_training st " +
        "JOIN student_mst s ON st.student_code = s.student_code " +
        "WHERE st.is_current = 1 " +
        "AND s.is_deleted = 0 " +
        "AND s.status_code NOT IN ('퇴관', '휴관')",
        nativeQuery = true)
    List<StudentTraining> findActiveTrainingsExcludingDeletedStudents();
    
}