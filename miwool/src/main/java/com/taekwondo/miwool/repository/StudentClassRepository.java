package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.StudentClass;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentClassRepository extends JpaRepository<StudentClass, Integer> {
    
    /**
     * 제자의 모든 수업 삭제
     * 퇴관 처리 시 사용
     */
    @Modifying
    @Query("DELETE FROM StudentClass sc WHERE sc.studentCode = :studentCode")
    int deleteByStudentCode(@Param("studentCode") String studentCode);
    
    /**
     * 수업별 학생 목록 조회
     * is_current 조건 체크 안 함 (default=1만 저장됨)
     */
    List<StudentClass> findByClassCode(String classCode);
    
    /**
     * 수련정보코드로 삭제
     */
    @Modifying
    @Query("DELETE FROM StudentClass sc WHERE sc.trainingInfoCode = :trainingInfoCode")
    void deleteByTrainingInfoCode(@Param("trainingInfoCode") Integer trainingInfoCode);
    
    /**
     * 수련정보코드로 시작일 일괄 수정
     */
    @Modifying
    @Query("UPDATE StudentClass sc SET sc.startDate = :startDate WHERE sc.trainingInfoCode = :trainingInfoCode")
    void updateStartDateByTrainingInfoCode(
            @Param("trainingInfoCode") Integer trainingInfoCode, 
            @Param("startDate") LocalDate startDate);
    
    /**
     * 수련정보코드로 수업 목록 조회
     */
    List<StudentClass> findByTrainingInfoCode(Integer trainingInfoCode);

    // 휴관 처리 시 현재 수업정보 end_date 업데이트
    @Modifying
    @Query("UPDATE StudentClass sc SET sc.endDate = :endDate " +
           "WHERE sc.studentCode = :studentCode AND sc.isCurrent = 1")
    int updateEndDateByStudentCode(
            @Param("studentCode") String studentCode,
            @Param("endDate") LocalDate endDate);
    
    /**
     * 앱 제자 상세 - 패키지별 수업 정보 조회
     * 결과: [패키지명, 수업명, 시작시간, 종료시간, 요일, 총교육비]
     */
    @Query(value = 
        "SELECT " +
        "    tm.package_name, " +
        "    cm.class_name, " +
        "    cm.start_time, " +
        "    cm.end_time, " +
        "    cm.day_of_week, " +
        "    stu.actual_price " +
        "FROM student_class sc " +
        "INNER JOIN class_mst cm ON sc.class_code = cm.class_code " +
        "INNER JOIN student_training st ON sc.training_info_code = st.training_info_code " +
        "INNER JOIN training_mst tm ON st.package_code = tm.package_code " +
        "INNER JOIN student_tuition stu ON sc.training_info_code = stu.training_info_code " +
        "WHERE sc.student_code = :studentCode " +
        "AND sc.is_current = 1 " +
        "AND stu.is_current = 1 " +
        "ORDER BY FIELD(cm.day_of_week, '월요일', '화요일', '수요일', '목요일', '금요일'), cm.start_time",
        nativeQuery = true)
    List<Object[]> findCurrentClassesByStudent(@Param("studentCode") String studentCode);
    
}