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
public class CounselDetailRespDto {
    
    private String counselCode;      // 상담코드
    private LocalDate counselDate;    // 상담일자
    private String studentName;       // 제자명
    private String counselType;       // 상담유형
    private String counselContent;    // 상담내용
    private String followUp;          // 후속조치내용
}