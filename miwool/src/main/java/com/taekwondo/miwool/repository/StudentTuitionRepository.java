package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentTuition;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentTuitionRepository extends JpaRepository<StudentTuition, Integer> {
    
    /**
     * 제자의 모든 교육비 정보 삭제
     * 퇴관 처리 시 사용
     */
    @Modifying
    @Query("DELETE FROM StudentTuition st WHERE st.studentCode = :studentCode")
    int deleteByStudentCode(@Param("studentCode") String studentCode);
    
    /**
     * 수련정보코드로 조회
     */
    Optional<StudentTuition> findByTrainingInfoCode(Integer trainingInfoCode);
    
    /**
     * 수련정보코드로 삭제
     */
    @Modifying
    @Query("DELETE FROM StudentTuition st WHERE st.trainingInfoCode = :trainingInfoCode")
    void deleteByTrainingInfoCode(@Param("trainingInfoCode") Integer trainingInfoCode);
    
    /**
     * 특정 수련정보의 현재 활성화된 교육비 정보 조회
     * 스케줄러에서 사용
     */
    Optional<StudentTuition> findByTrainingInfoCodeAndIsCurrent(
        Integer trainingInfoCode, Integer isCurrent);
    
}