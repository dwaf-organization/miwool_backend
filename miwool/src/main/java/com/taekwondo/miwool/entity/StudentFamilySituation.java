package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_family_situation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class StudentFamilySituation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapping_code")
    private Integer mappingCode; // 매핑코드 (PK, AI)

    @Column(name = "student_code", length = 20, nullable = false)
    private String studentCode; // 제자코드

    @Column(name = "family_situation_code", length = 20, nullable = false)
    private String familySituationCode; // 특이사항코드 (공통코드)

    @Column(name = "etc_value", length = 500)
    private String etcValue; // 기타값 (기타 선택 시 입력)

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 생성일
}