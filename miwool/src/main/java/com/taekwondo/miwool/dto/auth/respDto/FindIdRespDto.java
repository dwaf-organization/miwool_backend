package com.taekwondo.miwool.dto.auth.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindIdRespDto {
    
    private String dojangId;  // 찾은 아이디
    private String dojangName;  // 태권도장명
}