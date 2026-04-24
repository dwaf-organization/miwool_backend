package com.taekwondo.miwool.dto.app.student.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageUpdateReqDto {
    
    private String dojangCode; // 도장코드
    private String studentCode; // 제자코드
    private String profileImageUrl; // 프로필 이미지 URL
}