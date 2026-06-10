package com.taekwondo.miwool.dto.billing.reqDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentReqDto {
    
    @NotNull(message = "청구코드는 필수입니다")
    private Integer billingCode; // 청구코드
}