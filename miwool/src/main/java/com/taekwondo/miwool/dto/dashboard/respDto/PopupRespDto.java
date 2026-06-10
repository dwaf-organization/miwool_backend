package com.taekwondo.miwool.dto.dashboard.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupRespDto {
    
    private String date; // 조회 날짜 (YYYY-MM-DD)
    private List<StudentInfoDto> enrollment; // 입관 제자 목록
    private List<StudentInfoDto> withdrawal; // 퇴관 제자 목록
    private List<StudentInfoDto> trial; // 체험 제자 목록
    private List<StudentInfoDto> promotion; // 승단예정 제자 목록
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentInfoDto {
        private String studentCode;    // 제자코드
        private String studentName;    // 제자명
        private Integer genderCode;    // 성별코드
        private Integer age;           // 나이
        private String beltCode;       // 태권도 급수코드
        private String beltName;       // 태권도 급수명
        private String ropeBeltCode;   // 줄넘기 급수코드
        private String ropeBeltName;   // 줄넘기 급수명
    }
}