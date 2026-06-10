package com.taekwondo.miwool.dto.student.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBeltHistoryReqDto {
    
    @NotBlank(message = "제자코드는 필수입니다")
    private String studentCode;        // 제자코드 (필수)
    
    @NotNull(message = "변경일자는 필수입니다")
    private LocalDate changeDate;      // 변경일자 (필수)
    
    @NotNull(message = "경력개월수는 필수입니다")
    @Min(value = 0, message = "경력개월수는 0 이상이어야 합니다")
    private Integer careerMonths;      // 경력개월수 (필수)
    
    private String beltCode;           // 급수코드 (필수)
    private String ropeBeltCode;       // 줄넘기 급수코드 (nullable)
    private LocalDate promoDate;   // 승단예정일 (nullable)
}