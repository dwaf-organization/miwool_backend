package com.taekwondo.miwool.dto.admin.dojang.respDto;

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
public class DojangActivityRespDto {
    
    private String month; // 조회 월
    private String dojangName; // 도장명
    private CounselingDto counseling; // 상담 현황
    private EducationGuidanceDto educationGuidance; // 교육지도 현황
    private List<ActivityHistoryDto> activityHistory; // 활동이력
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CounselingDto {
        private Integer totalCount; // 총 상담건수
        private Double completionRate; // 완료율 (%)
        private Integer notCounseledStudentCount; // 미상담 제자 명수
        private Integer phoneCount; // 전화상담 건수
        private Integer messageCount; // 문자상담 건수
        private Integer inPersonCount; // 대면상담 건수
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EducationGuidanceDto {
        private Double overallProgressRate; // 전체 평균 진행율 (%)
        private List<GuidanceItemDto> items; // 항목별 진행률
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuidanceItemDto {
        private String itemName; // 항목명 (전화, 문자, 손편지 등)
        private Double progressRate; // 진행률 (%)
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityHistoryDto {
        private String eventName; // 행사명
        private String eventType; // 행사유형 (승단, 대회출전, 견학)
        private LocalDate eventDate; // 행사일정
        private Integer participantCount; // 참여인원수
    }
}