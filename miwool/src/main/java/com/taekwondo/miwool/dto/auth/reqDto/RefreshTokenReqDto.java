package com.taekwondo.miwool.dto.auth.reqDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenReqDto {
    
    @NotBlank(message = "Refresh Token을 입력해주세요")
    private String refreshToken;  // Refresh Token
}