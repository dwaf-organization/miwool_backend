package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Activity {
    
    @Id
    @Column(name = "activity_code", length = 20, nullable = false)
    private String activityCode; // 활동코드 (PK)
    
    @Column(name = "dojang_code", length = 20, nullable = false)
    private String dojangCode; // 도장코드
    
    @Column(name = "activity_name", length = 100, nullable = false)
    private String activityName; // 활동명
    
    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate; // 활동일자
    
    @Column(name = "activity_type", length = 50)
    private String activityType; // 활동유형
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 설명
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 등록일
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정일
}