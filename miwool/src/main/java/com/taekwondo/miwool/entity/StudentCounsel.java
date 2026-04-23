package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_counsel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class StudentCounsel {

    @Id
    @Column(name = "counsel_code", length = 20, nullable = false)
    private String counselCode; // 상담코드 (PK)

    @Column(name = "student_code", length = 20, nullable = false)
    private String studentCode; // 제자코드

    @Column(name = "counsel_date", nullable = false)
    private LocalDate counselDate; // 상담일자

    @Column(name = "counsel_type", length = 50)
    private String counselType; // 상담유형
    
    @Column(name = "counsel_content", columnDefinition = "TEXT")
    private String counselContent; // 상담내용

    @Column(name = "follow_up", columnDefinition = "TEXT")
    private String followUp; // 후속조치내용
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 등록일

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정일
}