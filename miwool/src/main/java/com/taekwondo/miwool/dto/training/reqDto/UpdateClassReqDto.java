package com.taekwondo.miwool.dto.training.reqDto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClassReqDto {
    
    @NotBlank(message = "수업코드는 필수입니다")
    private String classCode;            // 수업코드
    
    @NotBlank(message = "수업명은 필수입니다")
    private String className;            // 수업명
    
    @NotBlank(message = "요일은 필수입니다")
    private String dayOfWeek;            // 요일 (월요일, 화요일, ...)
    
    @NotNull(message = "시작시간은 필수입니다")
    private LocalTime startTime;         // 시작시간
    
    @NotNull(message = "종료시간은 필수입니다")
    private LocalTime endTime;           // 종료시간
    
    private String description;          // 수업설명
    
    @NotBlank(message = "사용여부는 필수입니다")
    @Pattern(regexp = "^[YN]$", message = "사용여부는 Y 또는 N이어야 합니다")
    private String useYn;                // 사용여부 (Y/N)
}