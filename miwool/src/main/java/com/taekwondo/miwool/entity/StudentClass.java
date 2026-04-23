package com.taekwondo.miwool.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_class")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentClass {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_info_code")
    private Integer classInfoCode;  // 수업정보코드 (AUTO_INCREMENT)
    
    @Column(name = "student_code", length = 20, nullable = false)
    private String studentCode;  // 제자코드
    
    @Column(name = "class_code", length = 20, nullable = false)
    private String classCode;  // 수업코드
    
    @Column(name = "training_info_code", nullable = false)
    private Integer trainingInfoCode;  // 교육비정보코드
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;  // 시작일 (이 수업 시작일)
    
    @Column(name = "end_date")
    private LocalDate endDate;  // 종료일 (변경 시 종료일)
    
    @Column(name = "is_current", nullable = false)
    private Integer isCurrent;  // 현재여부 (1=현재, 0=과거)
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;  // 생성일
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;  // 수정일
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isCurrent == null) {
            isCurrent = 1;  // 기본값: 현재
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}