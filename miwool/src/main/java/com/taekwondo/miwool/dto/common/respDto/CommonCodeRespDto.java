package com.taekwondo.miwool.dto.common.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeRespDto {
    
    private String commonCode;  // 공통코드
    private String codeName;  // 코드명
    private Integer codeOrder;  // 정렬순서
}