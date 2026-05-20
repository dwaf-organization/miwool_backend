package com.taekwondo.miwool.dto.admin.dashboard.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRankingDto {
    
    private String dojangName; // 도장명
    private Long studentCount; // 제자수
}