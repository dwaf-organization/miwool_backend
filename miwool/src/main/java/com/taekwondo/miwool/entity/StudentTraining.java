package com.taekwondo.miwool.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_training")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentTraining {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "training_info_code")
    private Integer trainingInfoCode;  // 수련정보번호 (AUTO_INCREMENT)
    
    @Column(name = "student_code", length = 20, nullable = false)
    private String studentCode;  // 제자코드
    
    @Column(name = "package_code", length = 20, nullable = false)
    private String packageCode;  // 패키지코드
    
    @Column(name = "use_vehicle", nullable = false)
    private Integer useVehicle;  // 차량이용여부 (1=이용, 0=미이용)
    
    @Column(name = "pickup_location", length = 200)
    private String pickupLocation;  // 탑승장소
    
    @Column(name = "dropoff_location", length = 200)
    private String dropoffLocation;  // 하차장소
    
    @Column(name = "handover_method", length = 100)
    private String handoverMethod;  // 보호자인계방식 (직접인계, 차량, 혼자귀가 등)
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;  // 시작일 (수련 시작일)
    
    @Column(name = "end_date")
    private LocalDate endDate;  // 종료일 (변경 시 종료일 기록)
    
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