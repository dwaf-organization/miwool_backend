package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentTraining;

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
    
    /**
     * 활성화된 수련정보 조회 (퇴관 학생 제외)
     * 스케줄러에서 청구서 발행 시 사용
     */
    @Query("SELECT st FROM StudentTraining st " +
           "JOIN Student s ON st.studentCode = s.studentCode " +
           "WHERE st.isCurrent = 1 AND (s.isDeleted = 0 OR s.isDeleted IS NULL)")
    List<StudentTraining> findActiveTrainingsExcludingDeletedStudents();
    
}