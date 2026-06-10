package com.taekwondo.miwool.dto.student.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalReqDto {
    
    @NotBlank(message = "제자코드는 필수입니다")
    private String studentCode;       // 제자코드
    
    private LocalDate withdrawalDate; // 퇴관일 (null이면 오늘)
    
    private String withdrawalReason;  // 퇴관사유 (null 허용)
}