package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "common_mst")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class CommonCode {
    
    @Id
    @Column(name = "common_code", length = 20, nullable = false)
    private String commonCode;  // 공통코드 (PK)
    
    @Column(name = "group_code", length = 20, nullable = false)
    private String groupCode;  // 그룹코드 (FK)
    
    @Column(name = "code_name", length = 50, nullable = false)
    private String codeName;  // 코드명
    
    @Column(name = "code_order", nullable = false)
    private Integer codeOrder;  // 코드순서
    
    @Column(name = "use_yn", length = 1, nullable = false)
    @Builder.Default
    private String useYn = "Y";  // 사용여부 (Y/N)
    
    @Column(name = "code_desc", columnDefinition = "TEXT")
    private String codeDesc;  // 코드설명
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성일
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;  // 수정일
}