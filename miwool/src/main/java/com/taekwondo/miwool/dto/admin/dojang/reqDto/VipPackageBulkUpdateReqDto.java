package com.taekwondo.miwool.dto.admin.dojang.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VipPackageBulkUpdateReqDto {
    
    private List<VipPackageUpdateReqDto> updates; // 업데이트 목록
}