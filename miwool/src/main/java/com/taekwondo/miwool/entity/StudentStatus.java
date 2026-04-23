package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class StudentStatus {
    
    @Id
    @Column(name = "status_history_code", length = 20, nullable = false)
    private String statusHistoryCode;  // 상태이력코드 (자동생성)
    
    @Column(name = "student_code", length = 20, nullable = false)
    private String studentCode;  // 제자코드
    
    @Column(name = "status_code", length = 20, nullable = false)
    private String statusCode;  // 상태코드 (재원/휴관/퇴관/체험/대기)
    
    @Column(name = "change_date", nullable = false)
    private LocalDate changeDate;  // 변경일
    
    @Column(name = "status_reason", columnDefinition = "TEXT")
    private String statusReason;  // 사유
    
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;  // 비고
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성일
}