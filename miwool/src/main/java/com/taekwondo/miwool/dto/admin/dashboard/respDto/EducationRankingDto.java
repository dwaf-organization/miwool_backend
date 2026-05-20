package com.taekwondo.miwool.dto.admin.dashboard.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationRankingDto {
    
    private String dojangName; // 도장명
    private Long totalStudents; // 전체제자수 (재원)
    private ExecutionRatesDto executionRates; // 각 교육별 실행률
    private Double averageScore; // 종합점수 (평균)
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExecutionRatesDto {
        private Double phone; // 전화
        private Double sms; // 문자
        private Double letter; // 손편지
        private Double snack; // 간식
        private Double certificate; // 상장
        private Double video; // 영상
        private Double observation; // 관찰지
    }
}