package com.taekwondo.miwool.dto.activity.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentActivityListItemDto {
    
    private String activityCode;           // 활동코드
    private String activityType;           // 활동유형
    private String activityName;           // 활동명
    private LocalDate activityDate;        // 활동일자
}