package com.taekwondo.miwool.dto.admin.dojang.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveReqDto {
    
    private String dojangCode; // 도장코드
}