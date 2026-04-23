package com.taekwondo.miwool.dto.management.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationItemsDto {
    
    private boolean phone;         // 전화
    private boolean message;       // 문자
    private boolean letter;        // 손편지
    private boolean snack;         // 간식
    private boolean video;         // 영상
    private boolean observation;   // 관찰지
    private boolean etc;           // 기타
    private String etcContent;     // 기타내용
}