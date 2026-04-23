package com.taekwondo.miwool.dto.management.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationDetailItemDto {
    
    private String type;           // "phone", "message", "letter", ...
    private String typeName;       // "전화", "문자", "손편지", ...
    private boolean checked;       // 실시 여부
    private String note;           // 메모 (없으면 빈 문자열)
}