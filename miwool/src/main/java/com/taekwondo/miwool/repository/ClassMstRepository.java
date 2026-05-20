package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.ClassMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassMstRepository extends JpaRepository<ClassMst, String> {
    
    /**
     * 도장별 수업 목록 조회 (최신순)
     */
    List<ClassMst> findByDojangCodeOrderByClassCodeAsc(String dojangCode);
    
    /**
     * 도장별 수업 목록 조회 (최신순)
     */
    List<ClassMst> findByDojangCodeOrderByStartTimeAsc(String dojangCode);
    
    
    /**
     * 도장별 수업 목록 조회 (과거순)
     */
    List<ClassMst> findByDojangCodeOrderByCreatedAtAsc(String dojangCode);
    
    /**
     * classCode 자동 생성용
     * 형식: MW26001-CLS001
     */
    Optional<ClassMst> findFirstByClassCodeStartingWithOrderByClassCodeDesc(String prefix);
    
    @Query(value = """
        SELECT 
            cm.class_code,
            cm.class_name,
            cm.start_time,
            cm.end_time,
            COUNT(sc.student_code) as participant_count
        FROM class_mst cm
        LEFT JOIN student_class sc 
            ON cm.class_code = sc.class_code 
            AND CURDATE() >= sc.start_date 
            AND (CURDATE() <= sc.end_date OR sc.end_date IS NULL)
        WHERE cm.dojang_code = :dojangCode
            AND cm.day_of_week = :dayOfWeek
        GROUP BY cm.class_code, cm.class_name, cm.start_time, cm.end_time
        ORDER BY cm.start_time ASC
        """, nativeQuery = true)
    List<Object[]> findTodayClassesByDayOfWeek(
            @Param("dojangCode") String dojangCode, 
            @Param("dayOfWeek") String dayOfWeek);
        
        
        @Query(value = """
            SELECT 
                s.gender_code,
                s.student_name,
                s.birth_date,
                s.grade,
                s.belt_code,
                c.code_name as belt_name,
                g.guardian_phone,
                s.student_phone,
                st.use_vehicle,
                st.pickup_location,
                st.dropoff_location,
                st.handover_method
            FROM student_class sc
            INNER JOIN student_mst s ON sc.student_code = s.student_code
            INNER JOIN student_training st ON sc.training_info_code = st.training_info_code
            LEFT JOIN common_mst c ON s.belt_code = c.common_code
            LEFT JOIN (
                SELECT sg.student_code, sg.guardian_code
                FROM student_guardian sg
                WHERE sg.student_code IN (
                    SELECT student_code 
                    FROM student_class 
                    WHERE class_code = :classCode
                )
                GROUP BY sg.student_code
                HAVING sg.guardian_code = MIN(sg.guardian_code)
            ) first_guardian ON s.student_code = first_guardian.student_code
            LEFT JOIN guardian_mst g ON first_guardian.guardian_code = g.guardian_code
            WHERE sc.class_code = :classCode
              AND CURDATE() >= sc.start_date
              AND (CURDATE() <= sc.end_date OR sc.end_date IS NULL)
            ORDER BY s.student_name
            """, nativeQuery = true)
        List<Object[]> findClassStudentDetails(@Param("classCode") String classCode);
            
        // 수업 목록 및 수강생 수 (use_yn='Y'인 것만)
        // (day_of_week, class_name, start_time, end_time, student_count)
        @Query(value = 
            "SELECT " +
            "    c.day_of_week, " +
            "    c.class_name, " +
            "    c.start_time, " +
            "    c.end_time, " +
            "    COUNT(DISTINCT sc.student_code) AS student_count " +
            "FROM class_mst c " +
            "LEFT JOIN student_class sc ON c.class_code = sc.class_code " +
            "    AND sc.is_current = 1 " +
            "WHERE c.dojang_code = :dojangCode " +
            "AND c.use_yn = 'Y' " +
            "GROUP BY c.day_of_week, c.class_name, c.start_time, c.end_time " +
            "ORDER BY " +
            "    FIELD(c.day_of_week, '월', '화', '수', '목', '금', '토', '일'), " +
            "    c.start_time",
            nativeQuery = true)
        List<Object[]> findClassesWithStudentCount(@Param("dojangCode") String dojangCode);
        
        
}