package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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
    @Column(name = "signup_alarm_code")
    private Integer signupAlarmCode; // 알림코드 (PK, AI)

    @Column(name = "dojang_code", length = 20, nullable = false)
    private String dojangCode; // 도장코드

    @Column(name = "dojang_name", length = 100, nullable = false)
    private String dojangName; // 도장명

    @Column(name = "master_name", length = 50, nullable = false)
    private String masterName; // 관장명

    @Column(name = "master_phone", length = 20, nullable = false)
    private String masterPhone; // 관장연락처

    @Column(name = "approval_status", nullable = false)
    @Builder.Default
    private Integer approvalStatus = 0; // 승인여부 (1=승인, 0=미승인)

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private Integer isRead = 0; // 읽음여부 (1=읽음, 0=안읽음)

    @Column(name = "read_at")
    private LocalDateTime readAt; // 읽은시간

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정일
}