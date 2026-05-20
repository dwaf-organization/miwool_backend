package com.taekwondo.miwool.dto.admin.student.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadRespDto {
    
    private Integer totalCount;    // 총 시도 건수
    private Integer successCount;  // 성공 건수
    private Integer failCount;     // 실패 건수
    private List<BulkUploadErrorDto> errors; // 실패 상세
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BulkUploadErrorDto {
        private Integer row;           // 엑셀 행 번호
        private String studentName;    // 제자명
        private String error;          // 오류 메시지
    }
}