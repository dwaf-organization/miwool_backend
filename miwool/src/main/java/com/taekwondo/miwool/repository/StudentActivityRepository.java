package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentActivityRepository extends JpaRepository<StudentActivity, String> {
    
    /**
     * studentActivityCode 자동 생성용 (도장코드-SAYYnnn)
     */
    Optional<StudentActivity> findFirstByStudentActivityCodeStartingWithOrderByStudentActivityCodeDesc(String prefix);
    
    /**
     * 활동의 참가자 목록 조회 (student_activity_code 순)
     */
    List<StudentActivity> findByActivityCodeOrderByStudentActivityCodeAsc(String activityCode);
    
    /**
     * 활동의 참가자 전체 삭제 (활동 수정/삭제 시 사용)
     */
    void deleteByActivityCode(String activityCode);
    
    /**
     * 제자가 활동에 참가했는지 확인
     */
    boolean existsByActivityCodeAndStudentCode(String activityCode, String studentCode);
}