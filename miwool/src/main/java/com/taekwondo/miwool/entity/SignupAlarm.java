package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "signup_alarm")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class SignupAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_code")
    private Integer alarmCode; // 알림코드 (PK, AI)

    @Column(name = "dojang_name", length = 100, nullable = false)
    private String dojangName; // 태권도장명

    @Column(name = "master_name", length = 50, nullable = false)
    private String masterName; // 관장명

    @Column(name = "master_phone", length = 20, nullable = false)
    private String masterPhone; // 관장연락처

    @Column(name = "request_status", length = 20, nullable = false)
    @Builder.Default
    private String requestStatus = "대기"; // 신청상태 (대기, 승인, 거절 등)

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Integer isRead = 0; // 읽음여부 (0: 안읽음, 1: 읽음)

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 신청일시
}