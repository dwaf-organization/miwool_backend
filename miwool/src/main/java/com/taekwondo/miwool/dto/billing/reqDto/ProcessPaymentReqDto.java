package com.taekwondo.miwool.dto.billing.reqDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPaymentReqDto {
    
    @NotNull(message = "청구코드는 필수입니다")
    private Integer billingCode; // 청구코드
    
    @NotNull(message = "납부일은 필수입니다")
    private LocalDate paymentDate; // 납부일
    
    @NotNull(message = "납부금액은 필수입니다")
    @Positive(message = "납부금액은 0보다 커야 합니다")
    private Integer paymentAmount; // 납부금액
    
    private String paymentMethod; // 납부방법
}