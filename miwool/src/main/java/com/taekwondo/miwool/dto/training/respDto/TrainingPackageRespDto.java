package com.taekwondo.miwool.dto.training.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingPackageRespDto {
    
    private String packageCode;          // 패키지코드
    private String packageName;          // 패키지명
    private int weeklyCount;             // 주횟수
    private int basePrice;               // 기본교육비
    private String description;          // 설명
    private String useYn;                // 사용여부 (백엔드에서 만들어두기)
    private LocalDateTime createdAt;     // 생성일
    private LocalDateTime updatedAt;     // 수정일
}