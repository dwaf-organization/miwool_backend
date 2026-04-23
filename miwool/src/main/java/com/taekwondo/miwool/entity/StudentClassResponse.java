package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_class_response")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EntityListeners(AuditingEntityListener.class)
public class StudentClassResponse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mappingCode;

    @Column(name = "student_code", length = 20, nullable = false)
    private String studentCode;

    @Column(name = "class_response_code", length = 20, nullable = false)
    private String classResponseCode; // 공통코드(PERS_LESSON)와 매핑

    @Column(name = "etc_value", length = 500)
    private String etcValue;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
