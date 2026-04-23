package com.taekwondo.miwool.dto.training.reqDto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTrainingPackageReqDto {
    
    @NotBlank(message = "패키지명은 필수입니다")
    private String packageName;          // 패키지명
    
    @NotNull(message = "주횟수는 필수입니다")
    @Positive(message = "주횟수는 양수여야 합니다")
    private Integer weeklyCount;         // 주횟수
    
    @NotNull(message = "기본교육비는 필수입니다")
    @Positive(message = "기본교육비는 양수여야 합니다")
    private Integer basePrice;           // 기본교육비
    
    private String description;          // 설명
}