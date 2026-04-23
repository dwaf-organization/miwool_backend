package com.taekwondo.miwool.dto.activity.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentActivityListRespDto {
    
    private List<StudentActivityListItemDto> activities;  // 활동 목록
    private int totalPages;                                // 전체 페이지 수
    private long totalElements;                            // 전체 항목 수
    private int currentPage;                               // 현재 페이지
    private int size;                                      // 페이지 크기
}