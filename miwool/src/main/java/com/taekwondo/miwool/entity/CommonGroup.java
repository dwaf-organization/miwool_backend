package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "common_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class CommonGroup {
    
    @Id
    @Column(name = "group_code", length = 20, nullable = false)
    private String groupCode;  // 그룹코드 (PK)
    
    @Column(name = "group_name", length = 50, nullable = false)
    private String groupName;  // 그룹명
    
    @Column(name = "group_order", nullable = false)
    private Integer groupOrder;  // 그룹순서
    
    @Column(name = "group_desc", columnDefinition = "TEXT")
    private String groupDesc;  // 그룹설명
    
    @Column(name = "use_yn", length = 1, nullable = false)
    @Builder.Default
    private String useYn = "Y";  // 사용여부 (Y/N)
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성일
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;  // 수정일
}