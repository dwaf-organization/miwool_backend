package com.taekwondo.miwool.dto.activity.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateActivityReqDto {
    
    @NotBlank(message = "활동코드는 필수입니다")
    private String activityCode;           // 활동코드 (필수)
    
    @NotBlank(message = "활동명은 필수입니다")
    private String activityName;           // 활동명 (필수)
    
    @NotNull(message = "활동일자는 필수입니다")
    private LocalDate activityDate;        // 활동일자 (필수)
    
    private String activityType;           // 활동유형 (선택)
    
    private String description;            // 설명 (선택)
    
    @NotNull(message = "참가 제자 목록은 필수입니다")
    private List<String> studentCodes;     // 참가 제자 코드 리스트 (필수)
}