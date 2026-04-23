package com.taekwondo.miwool.dto.common.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraitOptionDto {
    
    private String code;   // 코드값 (PB001, EM001 등)
    private String name;   // 코드명 (내향적, 자신감부족 등)
}