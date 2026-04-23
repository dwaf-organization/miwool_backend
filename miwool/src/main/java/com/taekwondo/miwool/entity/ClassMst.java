package com.taekwondo.miwool.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "class_mst")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassMst {
    
    @Id
    @Column(name = "class_code", length = 20)
    private String classCode;  // 수업코드 (CLS001)
    
    @Column(name = "dojang_code", length = 20, nullable = false)
    private String dojangCode;  // 태권도장코드
    
    @Column(name = "class_name", length = 100, nullable = false)
    private String className;  // 수업명 (월요일 오전반)
    
    @Column(name = "day_of_week", length = 10, nullable = false)
    private String dayOfWeek;  // 요일코드 (월, 화, 수, 목, 금)
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;  // 시작시간 (11:00)
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;  // 종료시간 (12:30)
    
    @Column(name = "description", length = 200)
    private String description;  // 수업설명
    
    @Column(name = "use_yn", length = 1, nullable = false)
    private String useYn;  // 사용여부 (Y/N)
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;  // 생성일
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;  // 수정일
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}