package com.taekwondo.miwool.dto.management.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationManagementItemDto {
    
    private String studentCode;        // 제자코드
    private int genderCode;            // 성별코드 (1=남, 2=여, 3=기타)
    private String studentName;        // 제자명
    private int age;                   // 나이 (한국나이)
    private String grade;              // 학년 (초1, 초2, ...)
    
    // 교육지도 실행 여부
    private boolean phoneYn;           // 전화
    private boolean messageYn;         // 문자
    private boolean letterYn;          // 손편지
    private boolean snackYn;           // 간식
    private boolean videoYn;           // 영상
    private boolean awardYn;           // 상장
    private boolean observationYn;     // 관찰지
    private boolean inbodyYn;          // 인바디
    private boolean etcYn;             // 기타
    private String etcContent;         // 기타내용 (note)
}