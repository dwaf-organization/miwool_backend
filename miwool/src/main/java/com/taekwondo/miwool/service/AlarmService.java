package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.alarm.respDto.AlarmRespDto;
import com.taekwondo.miwool.entity.Alarm;
import com.taekwondo.miwool.entity.Student;
import com.taekwondo.miwool.repository.AlarmRepository;
import com.taekwondo.miwool.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final StudentRepository studentRepository;

    /**
     * 알림 목록 조회 (5일 전까지, 안읽음 먼저)
     */
    @Transactional(readOnly = true)
    public List<AlarmRespDto> getAlarmList(String dojangCode) {
        log.info("알림 목록 조회: dojangCode={}", dojangCode);
        
        List<Alarm> alarms = alarmRepository.findRecentAlarms(dojangCode);
        
        List<AlarmRespDto> result = alarms.stream()
                .map(alarm -> {
                    // studentCode로 Student 조회
                    String studentName = null;
                    if (alarm.getStudentCode() != null) {
                        Student student = studentRepository.findById(alarm.getStudentCode()).orElse(null);
                        if (student != null) {
                            studentName = student.getStudentName();
                        }
                    }
                    
                    return AlarmRespDto.builder()
                            .alarmCode(alarm.getAlarmCode())
                            .alarmType(alarm.getAlarmType())
                            .targetName(alarm.getTargetName())
                            .studentCode(alarm.getStudentCode())
                            .studentName(studentName)
                            .alarmDate(alarm.getAlarmDate())
                            .alarmMessage(alarm.getAlarmMessage())
                            .isRead(alarm.getIsRead())
                            .createdAt(alarm.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
        
        log.info("알림 목록 조회 완료: {}건", result.size());
        return result;
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public void markAlarmAsRead(String dojangCode, Integer alarmCode) {
        log.info("알림 읽음 처리: dojangCode={}, alarmCode={}", dojangCode, alarmCode);
        
        Alarm alarm = alarmRepository.findById(alarmCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알림입니다."));
        
        if (!alarm.getDojangCode().equals(dojangCode)) {
            throw new IllegalArgumentException("권한이 없는 알림입니다.");
        }
        
        alarmRepository.markAsRead(alarmCode);
        log.info("알림 읽음 처리 완료");
    }
}