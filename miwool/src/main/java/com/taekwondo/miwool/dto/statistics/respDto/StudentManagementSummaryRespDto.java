package com.taekwondo.miwool.dto.statistics.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentManagementSummaryRespDto {
    
    private String month; // 조회 월 (YYYYMM)
    private PaymentDto payment; // 납부금
    private StudentStatusDto studentStatus; // 재원상태별 집계
    private RevenueSummaryDto revenueSummary; // 교육비결산
    private List<GenderRevenueDto> genderRevenue; // 성별매출
    private List<AgeRevenueDto> ageRevenue; // 연령별매출
    private List<PaymentMethodRevenueDto> paymentMethodRevenue; // 납부방법별 매출
    private EducationGuideDto educationGuide; // 교육지도
    
    // 납부금
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentDto {
        private Integer paid; // 납부완료 금액
        private Integer unpaid; // 미납 금액
    }
    
    // 재원상태별 집계
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentStatusDto {
        private StatusCountDto newEnrollment; // 신규입관
        private StatusCountDto enrolled; // 재원
        private StatusCountDto withdrawn; // 퇴관
        private StatusCountDto trial; // 체험
        private StatusCountDto suspension;    // 휴관
        private StatusCountDto reinstatement; // 복관
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusCountDto {
        private Integer current; // 이번달
        private Integer previous; // 전월
        private Integer change; // 증감수 (명)
    }
    
    // 교육비결산
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueSummaryDto {
        private Integer totalRevenue; // 해당월 총 매출액
        private List<PackageRevenueDto> packageRevenue; // 패키지별 매출
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackageRevenueDto {
        private String packageCode; // 패키지코드
        private String packageName; // 패키지명
        private Integer studentCount; // 인원수
        private Integer revenue; // 매출
        private Integer previousRevenue; // 전월 매출
        private Double changeRate; // 증감율 (%)
    }
    
    // 성별매출
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenderRevenueDto {
        private String gender; // 성별명 (남성/여성/기타)
        private Integer genderCode; // 성별코드
        private Integer studentCount; // 재원생 수
        private Integer revenue; // 매출
        private Integer previousRevenue; // 전월 매출
        private Double changeRate; // 증감율 (%)
    }
    
    // 연령별매출
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AgeRevenueDto {
        private String ageGroup; // 연령대 (유아/초등부/중등부/고등부/성인부)
        private Integer studentCount; // 재원생 수
        private Integer revenue; // 매출
        private Integer previousRevenue; // 전월 매출
        private Double changeRate; // 증감율 (%)
    }
    
    // 납부방법별 매출
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMethodRevenueDto {
        private String paymentMethod; // 납부방법
        private Integer amount;       // 납부금액
    }
    
    // 교육지도
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EducationGuideDto {
        private List<GuideStatsDto> statistics; // 유형별 실시 인원 + 미실시 명단
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuideStatsDto {
        private String guideType; // 교육지도 유형
        private Integer completedCount; // 실시 인원
        private List<NotCompletedDto> notCompleted; // 미실시 명단 (상위 10명)
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotCompletedDto {
        private String studentName; // 제자명
        private Integer genderCode; // 성별코드
    }
}