package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "guardian_mst")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Guardian {
    
    @Id
    @Column(name = "guardian_code", length = 20, nullable = false)
    private String guardianCode;  // 보호자고유코드 (자동생성)
    
    @Column(name = "dojang_code", length = 20, nullable = false)
    private String dojangCode;  // 태권도장고유코드
    
    @Column(name = "guardian_name", length = 50, nullable = false)
    private String guardianName;  // 보호자명
    
    @Column(name = "guardian_phone", length = 20, nullable = false)
    private String guardianPhone;  // 보호자연락처
    
    @Column(name = "guardian_emergency_phone", length = 20)
    private String guardianEmergencyPhone;  // 보호자비상연락처
    
    @Column(name = "guardian_birth_date")
    private LocalDate guardianBirthDate;  // 보호자생년월일
    
    @Column(name = "guardian_job", length = 50)
    private String guardianJob;  // 보호자직업
    
    @Column(name = "guardian_anniversary_date")
    private LocalDate guardianAnniversaryDate;  // 결혼기념일
    
    @Column(name = "guardian_request", columnDefinition = "TEXT")
    private String guardianRequest;  // 보호자요청사항
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성일
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;  // 수정일
}