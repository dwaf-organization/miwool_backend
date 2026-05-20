package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.SignupAlarm;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignupAlarmRepository extends JpaRepository<SignupAlarm, Integer> {

    /**
     * 읽지 않은 가입 신청 알림 목록 조회 (최신순)
     */
    List<SignupAlarm> findByIsReadOrderByCreatedAtDesc(Integer isRead);

    /**
     * 특정 승인 상태의 목록 조회 (최신순)
     */
    List<SignupAlarm> findByApprovalStatusOrderByCreatedAtDesc(Integer approvalStatus);

    /**
     * 읽지 않은 알림 총 개수 확인 (알림 배지용)
     */
    long countByIsRead(Integer isRead);
    
    /**
     * 관리자 알림 목록 조회
     * - 안읽은 데이터는 무조건 포함
     * - 읽은 데이터는 오늘 날짜만 포함
     * - readStatus 필터 적용 (전체, 읽음, 안읽음)
     */
    @Query(value = """
        SELECT * FROM signup_alarm
        WHERE (is_read = 0 OR (is_read = 1 AND DATE(read_at) = CURDATE()))
          AND (:readStatus = '전체' 
               OR (:readStatus = '읽음' AND is_read = 1)
               OR (:readStatus = '안읽음' AND is_read = 0))
        ORDER BY created_at DESC
        """, 
        countQuery = """
        SELECT COUNT(*) FROM signup_alarm
        WHERE (is_read = 0 OR (is_read = 1 AND DATE(read_at) = CURDATE()))
          AND (:readStatus = '전체' 
               OR (:readStatus = '읽음' AND is_read = 1)
               OR (:readStatus = '안읽음' AND is_read = 0))
        """,
        nativeQuery = true)
    Page<SignupAlarm> findSignupAlarmsForAdmin(
        @Param("readStatus") String readStatus,
        Pageable pageable
    );
    
    /**
     * 승인 상태별 알림 개수 조회
     */
    long countByApprovalStatus(Integer approvalStatus);
    
}