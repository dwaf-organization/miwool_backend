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
public class StudentActivityDetailRespDto {
    
    private String activityCode;           // 활동코드
    private LocalDate activityDate;        // 활동일자
    private String activityType;           // 활동유형
    private String activityName;           // 활동명
    private String activityArea;		   // 활동지역
    private String activityManager;		   //활동담당자
    private String description;            // 활동설명
}