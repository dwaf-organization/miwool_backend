package com.taekwondo.miwool.dto.app.auth.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRespDto {
    
    private String dojangCode; // 도장코드
    private String dojangName; // 도장명
}