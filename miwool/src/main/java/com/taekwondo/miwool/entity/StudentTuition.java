package com.taekwondo.miwool.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_tuition")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentTuition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tuition_info_code")
    private Integer tuitionInfoCode;  // 교육비정보코드 (AUTO_INCREMENT)
    
    @Column(name = "student_code", length = 20, nullable = false)
    private String studentCode;  // 제자코드
    
    @Column(name = "training_info_code", nullable = false)
    private Integer trainingInfoCode;
    
    @Column(name = "base_price", nullable = false)
    private Integer basePrice;  // 기본교육비 (패키지 기본 금액)
    
    @Column(name = "adjustment_amount")
    private Integer adjustmentAmount;  // 조정금액 (+/- 조정)
    
    @Column(name = "adjustment_detail", columnDefinition = "TEXT")
    private String adjustmentDetail;  // 조정상세 (조정 내역)
    
    @Column(name = "actual_price", nullable = false)
    private Integer actualPrice;  // 실제교육비 (base + adjustment)
    
    @Column(name = "billing_cycle_day", nullable = false)
    private Integer billingCycleDay;  // 청구주기일 (15, 1, 20 등 - 입관일의 일)
    
    @Column(name = "next_billing_date")
    private LocalDate nextBillingDate;  // 다음청구일 (다음 청구서 생성일)
    
    @Column(name = "apply_start_date", nullable = false)
    private LocalDate applyStartDate;  // 적용시작일
    
    @Column(name = "apply_end_date")
    private LocalDate applyEndDate;  // 적용종료일
    
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
        // 실제교육비 자동 계산
        if (actualPrice == null && basePrice != null) {
            actualPrice = basePrice + (adjustmentAmount != null ? adjustmentAmount : 0);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // 실제교육비 자동 계산
        if (basePrice != null) {
            actualPrice = basePrice + (adjustmentAmount != null ? adjustmentAmount : 0);
        }
    }
}