package com.taekwondo.miwool.repository;

import com.taekwondo.miwool.entity.SignupAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignupAlarmRepository extends JpaRepository<SignupAlarm, Integer> {

    /**
     * 읽지 않은 가입 신청 알림 목록 조회 (최신순)
     */
    List<SignupAlarm> findByIsReadOrderByCreatedAtDesc(Integer isRead);

    /**
     * 특정 신청 상태의 목록 조회 (최신순)
     */
    List<SignupAlarm> findByRequestStatusOrderByCreatedAtDesc(String requestStatus);

    /**
     * 읽지 않은 알림 총 개수 확인 (알림 배지용)
     */
    long countByIsRead(Integer isRead);
}