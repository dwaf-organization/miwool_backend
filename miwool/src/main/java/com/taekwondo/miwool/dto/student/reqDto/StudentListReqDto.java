package com.taekwondo.miwool.dto.student.reqDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentListReqDto {
    
    private String studentSearch;  // 제자명 or 제자코드 (부분검색)
    private String beltCode;       // 급수 코드 (최신값 기준)
    private Integer genderCode;    // 성별 (1=남, 2=여, 3=기타)
    private String gradeCode;      // 학년 ("유아", "초1", "초2", ..., "성인")
    private String statusCode;     // 재원상태 ("재원", "퇴관", "체험", 최신값 기준)
}