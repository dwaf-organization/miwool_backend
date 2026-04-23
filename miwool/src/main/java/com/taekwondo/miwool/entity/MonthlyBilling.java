package com.taekwondo.miwool.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_billing")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyBilling {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billing_code")
    private Integer billingCode;  // 청구번호 (AUTO_INCREMENT)
    
    @Column(name = "student_code", length = 20, nullable = false)
    private String studentCode;  // 제자코드
    
    @Column(name = "training_info_code", nullable = false)
    private Integer trainingInfoCode;  // 제자수련코드 (student_training FK)
    
    @Column(name = "billing_month", length = 7, nullable = false)
    private String billingMonth;  // 청구년월 (2024-04)
    
    @Column(name = "billing_date", nullable = false)
    private LocalDate billingDate;  // 청구일 (청구서 생성일 - 입관일/등록일)
    
    @Column(name = "training_start_date", nullable = false)
    private LocalDate trainingStartDate;  // 수련시작일 (실제 첫 수련일)
    
    @Column(name = "training_end_date", nullable = false)
    private LocalDate trainingEndDate;  // 수련종료일 (수련 종료 예정일)
    
    @Column(name = "billing_amount", nullable = false)
    private Integer billingAmount;  // 청구금액 (150,000)
    
    @Column(name = "billing_status", length = 20, nullable = false)
    private String billingStatus;  // 청구상태 (미납/완납/취소/중단)
    
    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 생성일
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;  // 납부일
    
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;  // 취소일
    
    @Column(name = "cancel_reason", length = 200)
    private String cancelReason;  // 취소사유
}