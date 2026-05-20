package com.taekwondo.miwool.dto.admin.alarm.respDto;

import com.taekwondo.miwool.common.dto.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupAlarmListRespDto {
    
    private List<SignupAlarmRespDto> content; // 알림 목록
    private Long totalElements; // 전체 데이터 수
    private PageInfo pageInfo; // 페이징 정보
}