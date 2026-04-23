package com.taekwondo.miwool.dto.guardian.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuardianInfoRespDto {
    
    private List<GuardianInfoDto> guardians;  // 보호자 목록
}