package com.taekwondo.miwool.dto.student.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeltHistoryListRespDto {
    
    // 현재 급수/경력 정보
    private String currentBelt;        // 현재 급수코드
    private String currentBeltName;    // 현재 급수명
    private int currentCareer;         // 현재 경력개월수
    
    // 이력 목록
    private List<BeltHistoryItemDto> histories;
}