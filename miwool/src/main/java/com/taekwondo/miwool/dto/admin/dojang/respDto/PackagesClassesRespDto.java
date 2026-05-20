package com.taekwondo.miwool.dto.admin.dojang.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagesClassesRespDto {
    
    private List<PackageDto> packages; // 패키지 목록
    private List<ClassDto> classes; // 수업 목록
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackageDto {
        private String packageName; // 패키지명
        private Integer classCountPerWeek; // 주 수업횟수
        private Integer monthlyFee; // 월 금액
        private Integer studentCount; // 수강생 수
        private String status; // 상태 (운영중/미운영)
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClassDto {
        private String classDay; // 수업요일
        private String className; // 수업명
        private String classTime; // 수업시간 (HH:mm~HH:mm)
        private Integer studentCount; // 현재 수강생 수
    }
}