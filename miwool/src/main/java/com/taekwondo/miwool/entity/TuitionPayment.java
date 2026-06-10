package com.taekwondo.miwool.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tuition_payment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TuitionPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_code")
    private Integer paymentCode; // 납부번호

    @Column(name = "billing_code", nullable = false)
    private Integer billingCode; // 청구번호

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate; // 납부일

    @Column(name = "payment_amount", nullable = false)
    private Integer paymentAmount; // 납부금액

    @Column(name = "payment_method", length = 20)
    private String paymentMethod; // 납부방법

    @Column(name = "receipt_phone", length = 20)
    private String receiptPhone; // 현금영수증 전화번호
    
    @Column(name = "note", length = 200)
    private String note; // 메모

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // 생성일
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}