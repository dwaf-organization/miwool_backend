package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_skill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class StudentSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id; // PK

    @Column(name = "student_code", length = 20, nullable = false)
    private String studentCode; // 제자코드

    @Column(name = "skill_code", length = 20, nullable = false)
    private String skillCode; // 기능습득속도 코드

    @Column(name = "etc_value", length = 200)
    private String etcValue; // 기타 입력값

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일
}