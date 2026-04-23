package com.taekwondo.miwool.dto.management.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationItemChangeDto {
    
    private boolean checked;       // true=체크, false=체크해제
    private String note;           // 메모 (null 가능)
}