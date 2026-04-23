package com.taekwondo.miwool.dto.management.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveEducationDetailReqDto {
    
    @NotBlank(message = "제자코드는 필수입니다")
    private String studentCode;
    
    @NotBlank(message = "년월은 필수입니다")
    private String yearMonth;  // "2026-04" 형식
    
    @NotNull(message = "변경 항목은 필수입니다")
    private Map<String, EducationItemChangeDto> changes;
    // 예: {"letter": {checked: true, note: "우수"}, "snack": {checked: false}}
}