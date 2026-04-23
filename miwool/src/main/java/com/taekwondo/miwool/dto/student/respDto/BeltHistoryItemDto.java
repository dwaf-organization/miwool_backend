package com.taekwondo.miwool.dto.student.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeltHistoryItemDto {
    
    private String historyCode;        // 이력코드
    private String beltCode;           // 급수코드
    private String beltName;           // 급수명
    private int careerMonths;          // 경력개월수
    private LocalDate changeDate;      // 이력변경일
}