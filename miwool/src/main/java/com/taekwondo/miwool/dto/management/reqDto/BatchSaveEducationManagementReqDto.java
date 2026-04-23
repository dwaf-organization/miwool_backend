package com.taekwondo.miwool.dto.management.reqDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchSaveEducationManagementReqDto {
    
    @NotBlank(message = "월은 필수입니다")
    private String month;  // "2024-04" 형식
    
    @NotNull(message = "학생 목록은 필수입니다")
    private List<StudentEducationChangeDto> students;  // 변경된 것만
}