package com.taekwondo.miwool.dto.guardian.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuardianInfoDto {
    
    private String guardianCode;              // 보호자코드
    private String guardianName;              // 보호자명
    private String relationship;              // 관계 (부/모/조부모/기타)
    private String guardianPhone;             // 보호자연락처
    private String guardianEmergencyPhone;    // 보호자비상연락처
    private LocalDate guardianBirthDate;      // 보호자생년월일
    private String guardianJob;               // 보호자직업
    private LocalDate guardianAnniversaryDate; // 결혼기념일
    private String guardianRequest;           // 보호자요청사항
}