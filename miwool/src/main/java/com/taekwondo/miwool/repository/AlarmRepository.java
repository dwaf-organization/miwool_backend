package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Integer> {
    // 특정 도장의 읽지 않은 알림 목록 조회 등 틀 구성
    List<Alarm> findByDojangCodeAndIsReadOrderByCreatedAtDesc(String dojangCode, Integer isRead);
    
    /**
     * 알림 조회 (5일 전까지, 안읽음 먼저)
     */
    @Query(value = 
        "SELECT * FROM alarm " +
        "WHERE dojang_code = :dojangCode " +
        "AND alarm_date >= DATE_SUB(CURDATE(), INTERVAL 5 DAY) " +
        "ORDER BY is_read ASC, alarm_code DESC, created_at DESC",
        nativeQuery = true)
    List<Alarm> findRecentAlarms(@Param("dojangCode") String dojangCode);
    
    /**
     * 오늘 날짜 알림 중복 체크
     */
    boolean existsByDojangCodeAndAlarmTypeAndTargetCodeAndAlarmDate(
        String dojangCode, String alarmType, String targetCode, LocalDate alarmDate);
    
    /**
     * 읽음 처리
     */
    @Modifying
    @Query("UPDATE Alarm a SET a.isRead = 1 WHERE a.alarmCode = :alarmCode")
    void markAsRead(@Param("alarmCode") Integer alarmCode);

    
}