package com.taekwondo.miwool.dto.auth.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckIdRespDto {
    
    private Boolean available;  // true: 사용 가능, false: 중복
    private String dojangId;  // 확인한 아이디
}