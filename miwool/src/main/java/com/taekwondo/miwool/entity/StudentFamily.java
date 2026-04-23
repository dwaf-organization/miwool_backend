package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_family")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class StudentFamily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_code")
    private Integer familyCode; // 가족코드 (PK, AI)

    @Column(name = "student_code", length = 20, nullable = false)
    private String studentCode; // 제자코드

    @Column(name = "family_composition", length = 500)
    private String familyComposition; // 가족구성 (부모, 조부모, 형제1 등)

    @Column(name = "family_name", length = 500)
    private String familyName; // 가족이름 (부:홍길동, 동생:김동생 등)

    @Column(name = "family_birth", length = 500)
    private String familyBirth; // 가족생년월일 (부:73.01.01 등)

    @Column(name = "sibling_count")
    private Integer siblingCount; // 형제수

    @Column(name = "is_also_student", length = 500)
    private String isAlsoStudent; // 함께다니는제자

    @Column(name = "primary_caregiver", length = 20)
    private String primaryCaregiver; // 주양육자

    @Column(name = "family_note", columnDefinition = "TEXT")
    private String familyNote; // 가정특이사항메모

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정일
}