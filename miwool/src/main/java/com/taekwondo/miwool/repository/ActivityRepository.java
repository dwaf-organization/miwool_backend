package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, String> {
    
    /**
     * activityCode 자동 생성용 (도장코드-AYYnnn)
     */
    Optional<Activity> findFirstByActivityCodeStartingWithOrderByActivityCodeDesc(String prefix);
    
    /**
     * 제자가 참여한 활동 목록 조회 (페이징)
     */
    @Query("SELECT DISTINCT a FROM Activity a " +
           "JOIN StudentActivity sa ON sa.activityCode = a.activityCode " +
           "WHERE sa.studentCode = :studentCode")
    Page<Activity> findActivitiesByStudentCode(@Param("studentCode") String studentCode, Pageable pageable);
    
    /**
     * 활동내역 조회 (필터링 - Native Query)
     * 조건: 활동명(코드/명 부분조회), 활동유형, 활동기간
     */
    @Query(value = "SELECT DISTINCT a.* " +
                   "FROM activity a " +
                   "WHERE 1=1 " +
                   "  AND (:activityCodeOrName IS NULL " +
                   "       OR a.activity_code LIKE CONCAT('%', :activityCodeOrName, '%') " +
                   "       OR a.activity_name LIKE CONCAT('%', :activityCodeOrName, '%')) " +
                   "  AND (:activityType IS NULL OR a.activity_type = :activityType) " +
                   "  AND (:startDate IS NULL OR a.activity_date >= :startDate) " +
                   "  AND (:endDate IS NULL OR a.activity_date <= :endDate) " +
                   "ORDER BY a.activity_date DESC",
           nativeQuery = true)
    List<Activity> findActivitiesWithFilters(
            @Param("activityCodeOrName") String activityCodeOrName,
            @Param("activityType") String activityType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Sort sort);
    
    // 월별 활동 목록 (참여인원 포함)
    // (activity_name, activity_type, activity_date, participant_count)
    @Query(value = 
        "SELECT " +
        "    a.activity_name, " +
        "    a.activity_type, " +
        "    a.activity_date, " +
        "    COUNT(sa.student_code) AS participant_count " +
        "FROM activity a " +
        "LEFT JOIN student_activity sa ON a.activity_code = sa.activity_code " +
        "WHERE a.dojang_code = :dojangCode " +
        "AND DATE_FORMAT(a.activity_date, '%Y%m') = :month " +
        "GROUP BY a.activity_code, a.activity_name, a.activity_type, a.activity_date " +
        "ORDER BY a.activity_date",
        nativeQuery = true)
    List<Object[]> findActivitiesWithParticipantsByMonth(
        @Param("dojangCode") String dojangCode,
        @Param("month") String month);
    
    
}