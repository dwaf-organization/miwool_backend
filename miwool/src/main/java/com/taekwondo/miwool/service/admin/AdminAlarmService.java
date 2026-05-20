package com.taekwondo.miwool.service.admin;

import com.taekwondo.miwool.common.dto.PageInfo;
import com.taekwondo.miwool.dto.admin.alarm.respDto.SignupAlarmListRespDto;
import com.taekwondo.miwool.dto.admin.alarm.respDto.SignupAlarmRespDto;
import com.taekwondo.miwool.entity.SignupAlarm;
import com.taekwondo.miwool.repository.SignupAlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAlarmService {

    private final SignupAlarmRepository signupAlarmRepository;

    /**
     * 관리자 가입신청 알림 목록 조회
     * - 안읽은 데이터는 무조건 포함
     * - 읽은 데이터는 오늘 날짜만 포함
     */
    @Transactional(readOnly = true)
    public SignupAlarmListRespDto getSignupAlarms(String readStatus, int page, int size) {
        log.info("관리자 가입신청 알림 목록 조회: readStatus={}, page={}, size={}", readStatus, page, size);
        
        // 페이징 설정
        Pageable pageable = PageRequest.of(page, size);
        
        // 알림 목록 조회
        Page<SignupAlarm> alarmPage = signupAlarmRepository.findSignupAlarmsForAdmin(readStatus, pageable);
        
        // Entity → DTO 변환
        List<SignupAlarmRespDto> content = alarmPage.getContent().stream()
                .map(alarm -> SignupAlarmRespDto.builder()
                        .signupAlarmCode(alarm.getSignupAlarmCode())
                        .createdAt(alarm.getCreatedAt())
                        .dojangName(alarm.getDojangName())
                        .masterName(alarm.getMasterName())
                        .masterPhone(alarm.getMasterPhone())
                        .isRead(alarm.getIsRead())
                        .approvalStatus(alarm.getApprovalStatus())
                        .build())
                .collect(Collectors.toList());
        
        // PageInfo 생성
        PageInfo pageInfo = PageInfo.builder()
                .currentPage(alarmPage.getNumber())
                .totalPages(alarmPage.getTotalPages())
                .size(alarmPage.getSize())
                .hasNext(alarmPage.hasNext())
                .hasPrevious(alarmPage.hasPrevious())
                .build();
        
        log.info("관리자 가입신청 알림 목록 조회 완료: 총 {}건, {}페이지", alarmPage.getTotalElements(), alarmPage.getTotalPages());
        
        return SignupAlarmListRespDto.builder()
                .content(content)
                .totalElements(alarmPage.getTotalElements())
                .pageInfo(pageInfo)
                .build();
    }
    
    /**
     * 가입신청 알림 읽음처리
     */
    @Transactional
    public void markAsRead(Integer signupAlarmCode) {
        log.info("가입신청 알림 읽음처리: signupAlarmCode={}", signupAlarmCode);
        
        // 알림 조회
        SignupAlarm alarm = signupAlarmRepository.findById(signupAlarmCode)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));
        
        // 읽음처리
        alarm.setIsRead(1);
        alarm.setReadAt(LocalDateTime.now());
        
        signupAlarmRepository.save(alarm);
        
        log.info("가입신청 알림 읽음처리 완료: signupAlarmCode={}", signupAlarmCode);
    }
    
}