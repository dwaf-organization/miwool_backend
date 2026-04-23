package com.taekwondo.miwool.dto.guardian.reqDto;

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
public class SaveGuardianReqDto {
    
    private String guardianCode;              // 보호자코드 (null: 생성, 값: 수정)
    
    @NotBlank(message = "제자코드는 필수입니다")
    private String studentCode;               // 제자코드 (필수)
    
    @NotBlank(message = "보호자명은 필수입니다")
    private String guardianName;              // 보호자명 (필수)
    
    @NotBlank(message = "관계는 필수입니다")
    private String relationship;              // 관계 (필수)
    
    @NotBlank(message = "연락처는 필수입니다")
    private String guardianPhone;             // 보호자연락처 (필수)
    
    private String guardianEmergencyPhone;    // 보호자비상연락처 (선택)
    private LocalDate guardianBirthDate;      // 보호자생년월일 (선택)
    private String guardianJob;               // 보호자직업 (선택)
    private LocalDate guardianAnniversaryDate; // 결혼기념일 (선택)
    private String guardianRequest;           // 보호자요청사항 (선택)
}