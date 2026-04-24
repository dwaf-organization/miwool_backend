package com.taekwondo.miwool.dto.app.student.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetailRespDto {
    
    // 기본정보
    private BasicInfoDto basicInfo;
    
    // 패키지정보 리스트
    private List<PackageInfoDto> packageInfo;
    
    // 보호자정보
    private GuardianInfoDto guardianInfo;
    
    // 상담실시여부
    private CounselStatusDto counselStatus;
    
    // 교육비납부여부
    private BillingStatusDto billingStatus;
    
    // 제자특성
    private TraitsDto traits;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicInfoDto {
        private String profileImageUrl; // 프로필사진
        private Integer genderCode; // 성별코드
        private String studentName; // 제자명
        private String statusCode; // 재원상태
        private Integer age; // 나이
        private String grade; // 학년
        private LocalDate registDate; // 입관일
        private LocalDate birthDate; // 생년월일
        private String studentPhone; // 연락처
        private String beltCode; // 급수코드
        private String beltName; // 급수명
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackageInfoDto {
        private String packageName; // 패키지명
        private Integer totalFee; // 총교육비
        private List<ClassInfoDto> classes; // 수업 리스트
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassInfoDto {
        private String className; // 수업명
        private String classTime; // 수업시간 (HH:mm ~ HH:mm)
        private String dayOfWeek; // 요일
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuardianInfoDto {
        private String guardianName; // 보호자명
        private String relationship; // 관계
        private String guardianPhone; // 연락처
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CounselStatusDto {
        private String status; // 실시/미실시
        private LocalDate counselDate; // 실시일자
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingStatusDto {
        private String status; // 납부완료/미납
        private Integer billingAmount; // 청구금액
        private LocalDateTime processedDate; // 처리일자 (paidAt)
        private LocalDate billingDate; // 청구일
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TraitsDto {
        private List<String> personalityBasic; // 기본성향명 리스트
        private String personalityBasicEtc; // 기본성향 기타값
        private List<String> emotion; // 정서특성명 리스트
        private String emotionEtc; // 정서특성 기타값
        private List<String> sociability; // 사회성특성명 리스트
        private String sociabilityEtc; // 사회성특성 기타값
        private List<String> lessonResponse; // 수업반응명 리스트
        private String lessonResponseEtc; // 수업반응 기타값
        private List<String> changeNeed; // 변화필요부분명 리스트
        private String changeNeedEtc; // 변화필요부분 기타값
        private List<String> strength; // 강점명 리스트
        private String strengthEtc; // 강점 기타값
    }
}