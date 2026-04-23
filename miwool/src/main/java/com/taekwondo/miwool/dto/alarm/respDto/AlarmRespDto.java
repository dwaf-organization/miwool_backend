package com.taekwondo.miwool.dto.alarm.respDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmRespDto {
    
    private Integer alarmCode; // 알림코드
    private String alarmType; // 알림타입 (제자 생일, 보호자 생일, 보호자 결혼기념일)
    private String targetName; // 대상이름 (보호자는 "김영희(모)" 형식)
    private String studentCode; // 제자코드
    private String studentName; // 제자명
    private LocalDate alarmDate; // 알림날짜
    private String alarmMessage; // 알림메시지
    private Integer isRead; // 읽음여부 (0=읽지않음, 1=읽음)
    private LocalDateTime createdAt; // 생성일
}