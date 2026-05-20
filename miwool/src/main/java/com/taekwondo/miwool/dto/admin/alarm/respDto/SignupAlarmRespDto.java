package com.taekwondo.miwool.dto.admin.alarm.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupAlarmRespDto {
    
    private Integer signupAlarmCode; // 알림코드
    private LocalDateTime createdAt; // 생성일
    private String dojangName; // 도장명
    private String masterName; // 관장명
    private String masterPhone; // 연락처
    private Integer isRead; // 읽음여부 (0=안읽음, 1=읽음)
    private Integer approvalStatus; // 승인여부 (0=미승인, 1=승인)
}