package com.taekwondo.miwool.dto.counsel.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounselListItemDto {
    
    private String counselCode;      // 상담코드
    private String counselType;       // 상담유형
    private LocalDate counselDate;    // 상담일자
}