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
public class StudentListRespDto {
    
    private List<StudentDto> students; // 제자 목록
    private Long totalElements; // 전체 개수
    private Integer totalPages; // 전체 페이지 수
    private Integer currentPage; // 현재 페이지
    private Integer size; // 페이지 크기
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentDto {
        private String studentCode; // 제자코드
        private String studentName; // 제자명
        private Integer genderCode; // 성별코드
        private Integer age; // 나이
        private String grade; // 학년
        private String beltName; // 급수명
        private String studentPhone; // 연락처
        private String statusCode; // 재원상태
        private LocalDate registDate; // 입관일
        private LocalDateTime deletedAt; // 퇴관일
    }
}