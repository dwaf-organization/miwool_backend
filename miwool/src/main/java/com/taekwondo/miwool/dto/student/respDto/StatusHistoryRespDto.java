package com.taekwondo.miwool.dto.student.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusHistoryRespDto {
    
    private List<StatusHistoryItemDto> histories;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusHistoryItemDto {
        private String statusHistoryCode; // 이력코드
        private String statusCode;        // 상태코드 (재원/휴관/복관/퇴관/체험)
        private LocalDate changeDate;     // 변경일
        private String statusReason;      // 사유 (null 가능)
        private String note;              // 비고 (null 가능)
    }
}