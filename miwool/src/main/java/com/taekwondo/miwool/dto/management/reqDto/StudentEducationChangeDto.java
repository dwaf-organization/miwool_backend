package com.taekwondo.miwool.dto.management.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentEducationChangeDto {
    
    private String studentCode;            // 제자코드
    private Map<String, Boolean> changes;  // 변경된 항목들
    // key: phone, message, letter, snack, video, observation, etc
    // value: true(체크/추가), false(체크해제/삭제)
    
    private String etcContent;             // 기타내용 (etc=true일 때만)
}