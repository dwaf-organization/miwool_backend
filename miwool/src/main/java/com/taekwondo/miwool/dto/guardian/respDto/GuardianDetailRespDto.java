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
public class GuardianDetailRespDto {
    
    private String guardianCode; // 보호자코드
    private String guardianName; // 보호자명
    private String relationship; // 관계
    private String guardianPhone; // 연락처
    private String guardianEmergencyPhone; // 비상연락처
    private LocalDate guardianBirthDate; // 생년월일
    private String guardianJob; // 직업
    private LocalDate guardianAnniversaryDate; // 결혼기념일
    private String guardianRequest; // 요청사항
}