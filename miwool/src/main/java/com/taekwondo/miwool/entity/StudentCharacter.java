package com.taekwondo.miwool.entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_character")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EntityListeners(AuditingEntityListener.class)
public class StudentCharacter {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mappingCode;

    @Column(name = "student_code", length = 20, nullable = false)
    private String studentCode;

    @Column(name = "character_code", length = 20, nullable = false)
    private String characterCode; // 공통코드(PERS_BASIC)와 매핑

    @Column(name = "etc_value", length = 500)
    private String etcValue;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}