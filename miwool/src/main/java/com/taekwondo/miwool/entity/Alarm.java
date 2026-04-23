package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "alarm")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_code")
    private Integer alarmCode; // 알림코드 (Auto Increment)

    @Column(name = "dojang_code", length = 20, nullable = false)
    private String dojangCode; // 도장코드

    @Column(name = "alarm_type", length = 20, nullable = false)
    private String alarmType; // 알림타입 (제자생일, 보호자생일 등)

    @Column(name = "target_code", length = 20)
    private String targetCode; // 대상코드 (제자/보호자코드)

    @Column(name = "target_name", length = 50, nullable = false)
    private String targetName; // 대상이름

    @Column(name = "relation", length = 20)
    private String relation; // 관계 (어머니, 아버지 등)

    @Column(name = "student_code", length = 20)
    private String studentCode; // 제자코드

    @Column(name = "alarm_date", nullable = false)
    private LocalDate alarmDate; // 알림날짜 / 이벤트 발생일

    @Column(name = "alarm_message", columnDefinition = "TEXT")
    private String alarmMessage; // 알림메시지

    @Column(name = "is_read")
    @Builder.Default
    private Integer isRead = 0; // 읽음여부 (0=읽지않음, 1=읽음)

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 생성일
}