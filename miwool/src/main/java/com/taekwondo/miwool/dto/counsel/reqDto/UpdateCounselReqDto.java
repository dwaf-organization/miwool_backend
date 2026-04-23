package com.taekwondo.miwool.dto.counsel.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCounselReqDto {
    
    @NotNull(message = "상담코드는 필수입니다")
    private String counselCode;          // 상담코드 (필수)
    
    @NotBlank(message = "제자코드는 필수입니다")
    private String studentCode;           // 제자코드 (필수)
    
    @NotNull(message = "상담일자는 필수입니다")
    private LocalDate counselDate;        // 상담일자 (필수)
    
    private String counselType;           // 상담유형 (선택)
    
    private String counselContent;        // 상담내용
    
    private String followUp;              // 후속조치내용
}