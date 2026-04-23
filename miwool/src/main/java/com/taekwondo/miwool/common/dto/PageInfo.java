package com.taekwondo.miwool.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
    
    private Integer currentPage;   // 현재 페이지 번호
    private Integer totalPages;    // 전체 페이지 수
    private Integer size;          // 페이지 크기
    private Boolean hasNext;       // 다음 페이지 존재 여부
    private Boolean hasPrevious;   // 이전 페이지 존재 여부
}