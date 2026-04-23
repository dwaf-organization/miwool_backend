package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_management")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class StudentManagement {

    @Id
    @Column(name = "management_code", length = 20, nullable = false)
    private String managementCode; // 교육관리코드 (PK)

    @Column(name = "student_code", length = 20, nullable = false)
    private String studentCode; // 제자코드

    @Column(name = "management_type_code", length = 20, nullable = false)
    private String managementTypeCode; // 교육관리유형코드 (전화/문자/간식 등)

    @Column(name = "executed_date")
    private LocalDate executedDate; // 실시일

    @Column(name = "note", columnDefinition = "TEXT")
    private String note; // 메모

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정일
}