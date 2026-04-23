package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_belt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class StudentBelt {
    
    @Id
    @Column(name = "belt_history_code", length = 20, nullable = false)
    private String beltHistoryCode;  // 등급이력코드 (자동생성)
    
    @Column(name = "student_code", length = 20, nullable = false)
    private String studentCode;  // 제자코드
    
    @Column(name = "belt_code", length = 20, nullable = false)
    private String beltCode;  // 등급 (공통코드)
    
    @Column(name = "taekwondo_months")
    private Integer taekwondoMonths;  // 경력 (개월수)
    
    @Column(name = "acquired_at", nullable = false)
    private LocalDate acquiredAt;  // 취득일
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성일
}