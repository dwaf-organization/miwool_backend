package com.taekwondo.miwool.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "training_mst")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingMst {
    
    @Id
    @Column(name = "package_code", length = 20)
    private String packageCode;  // 패키지코드 (PKG001)
    
    @Column(name = "dojang_code", length = 20, nullable = false)
    private String dojangCode;  // 태권도장코드
    
    @Column(name = "package_name", length = 50, nullable = false)
    private String packageName;  // 패키지명 (주3회 정규반)
    
    @Column(name = "weekly_count", nullable = false)
    private Integer weeklyCount;  // 주횟수 (3, 5 등)
    
    @Column(name = "base_price", nullable = false)
    private Integer basePrice;  // 기본교육비 (150,000원)
    
    @Column(name = "description", length = 200)
    private String description;  // 설명
    
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