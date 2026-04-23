package com.taekwondo.miwool.dto.management.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationDetailRespDto {
    
    // 제자 정보
    private String studentCode;        // 제자코드
    private String studentName;        // 제자명
    private int genderCode;            // 성별코드 (1=남, 2=여, 3=기타)
    private int age;                   // 나이 (한국나이)
    private String grade;              // 학년 (초1, 초2, ...)
    
    // 교육지도 정보
    private String yearMonth;          // 년월 ("2026-04")
    private List<EducationDetailItemDto> items;  // 7개 항목 (전체)
}