package com.taekwondo.miwool.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_activity",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"activity_code", "student_code"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentActivity {
    
    @Id
    @Column(name = "student_activity_code", length = 20, nullable = false)
    private String studentActivityCode; // 제자활동코드 (PK)
    
    @Column(name = "activity_code", length = 20, nullable = false)
    private String activityCode; // 활동코드 (FK)
    
    @Column(name = "student_code", length = 20, nullable = false)
    private String studentCode; // 제자코드 (FK)
}