package com.taekwondo.miwool.dto.activity.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDetailRespDto {
    
    // 활동 정보
    private String activityCode;           // 활동코드
    private String activityName;           // 활동명
    private LocalDate activityDate;        // 활동일자
    private String activityType;           // 활동유형
    private String description;            // 설명
    
    // 참가자 목록
    private List<ParticipantDto> participants;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipantDto {
        private String studentCode;        // 제자코드
        private String studentName;        // 제자명
        private String beltCode;           // 급수코드
        private Integer genderCode;         // 성별코드
        private Integer age;				// 나이
        private String grade;             // 학년
    }
}