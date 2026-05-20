package com.taekwondo.miwool.service.admin;

import com.taekwondo.miwool.dto.admin.dojang.respDto.DojangActivityRespDto;
import com.taekwondo.miwool.dto.admin.dojang.respDto.DojangActivityRespDto.ActivityHistoryDto;
import com.taekwondo.miwool.dto.admin.dojang.respDto.DojangActivityRespDto.CounselingDto;
import com.taekwondo.miwool.dto.admin.dojang.respDto.DojangActivityRespDto.EducationGuidanceDto;
import com.taekwondo.miwool.dto.admin.dojang.respDto.DojangActivityRespDto.GuidanceItemDto;
import com.taekwondo.miwool.entity.Dojang;
import com.taekwondo.miwool.repository.ActivityRepository;
import com.taekwondo.miwool.repository.DojangRepository;
import com.taekwondo.miwool.repository.StudentCounselRepository;
import com.taekwondo.miwool.repository.StudentManagementRepository;
import com.taekwondo.miwool.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminDojangActivityService {
    
    @Autowired
    private StudentCounselRepository studentCounselRepository;
    
    @Autowired
    private StudentManagementRepository studentManagementRepository;
    
    @Autowired
    private ActivityRepository activityRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private DojangRepository dojangRepository;
    
    @Transactional(readOnly = true)
    public DojangActivityRespDto getDojangActivity(String dojangCode, String month) {
        log.info("도장 활동현황 조회 시작: dojangCode={}, month={}", dojangCode, month);
        
        // month 형식 변환: 2026-05 -> 202605
        String monthFormatted = month.replace("-", "");
        
        // 도장명 조회
        Dojang dojang = dojangRepository.findById(dojangCode)
                .orElseThrow(() -> new IllegalArgumentException("도장을 찾을 수 없습니다."));
        String dojangName = dojang.getDojangName();
        
        // 1. 상담 현황 집계
        CounselingDto counseling = getCounselingData(dojangCode, monthFormatted);
        
        // 2. 교육지도 현황 집계
        EducationGuidanceDto educationGuidance = getEducationGuidanceData(dojangCode, monthFormatted);
        
        // 3. 활동이력 조회
        List<ActivityHistoryDto> activityHistory = getActivityHistoryData(dojangCode, monthFormatted);
        
        log.info("도장 활동현황 조회 완료: dojangCode={}, month={}", dojangCode, month);
        
        return DojangActivityRespDto.builder()
                .month(month) // 원본 형식 그대로 반환
                .dojangName(dojangName)
                .counseling(counseling)
                .educationGuidance(educationGuidance)
                .activityHistory(activityHistory)
                .build();
    }
    
    // 상담 현황 집계
    private CounselingDto getCounselingData(String dojangCode, String month) {
        // 총 상담 건수
        int totalCount = studentCounselRepository.countTotalCounselingByMonth(dojangCode, month);
        
        // 전체 재원생 수
        int totalStudents = studentRepository.countByDojangCodeAndStatusCode(dojangCode, "재원");
        
        // 완료율 계산 (상담받은 학생 수 / 전체 재원생)
        double completionRate = totalStudents > 0 ? (totalCount * 100.0 / totalStudents) : 0.0;
        completionRate = Math.round(completionRate * 10.0) / 10.0;
        
        // 미상담 제자 수
        int notCounseledStudentCount = studentCounselRepository.countNotCounseledStudents(dojangCode, month);
        
        // 상담 유형별 건수 (counsel_type, count)
        List<Object[]> counselingByType = studentCounselRepository.countCounselingByType(dojangCode, month);
        Map<String, Integer> counselingMap = new HashMap<>();
        for (Object[] row : counselingByType) {
            String counselType = (String) row[0];
            Integer count = ((Number) row[1]).intValue();
            counselingMap.put(counselType, count);
        }
        
        return CounselingDto.builder()
                .totalCount(totalCount)
                .completionRate(completionRate)
                .notCounseledStudentCount(notCounseledStudentCount)
                .phoneCount(counselingMap.getOrDefault("전화상담", 0))
                .messageCount(counselingMap.getOrDefault("문자상담", 0))
                .inPersonCount(counselingMap.getOrDefault("대면상담", 0))
                .build();
    }
    
    // 교육지도 현황 집계
    private EducationGuidanceDto getEducationGuidanceData(String dojangCode, String month) {
        // 재원생 수 확인
        int totalStudents = studentRepository.countByDojangCodeAndStatusCode(dojangCode, "재원");
        
        // 재원생이 없으면 빈 데이터 반환
        if (totalStudents == 0) {
            return EducationGuidanceDto.builder()
                    .overallProgressRate(0.0)
                    .items(new ArrayList<>())
                    .build();
        }
        
        // 항목별 진행률 (code_name, progress_rate)
        List<Object[]> guidanceProgress = studentManagementRepository.calculateGuidanceProgressByItem(dojangCode, month);
        
        List<GuidanceItemDto> items = guidanceProgress.stream()
                .map(row -> {
                    String itemName = (String) row[0];
                    Double progressRate = ((Number) row[1]).doubleValue();
                    progressRate = Math.round(progressRate * 10.0) / 10.0;
                    
                    return GuidanceItemDto.builder()
                            .itemName(itemName)
                            .progressRate(progressRate)
                            .build();
                })
                .collect(Collectors.toList());
        
        // 전체 평균 진행률
        double overallProgressRate = items.isEmpty() ? 0.0 : 
                items.stream()
                     .mapToDouble(GuidanceItemDto::getProgressRate)
                     .average()
                     .orElse(0.0);
        overallProgressRate = Math.round(overallProgressRate * 10.0) / 10.0;
        
        return EducationGuidanceDto.builder()
                .overallProgressRate(overallProgressRate)
                .items(items)
                .build();
    }
    
    // 활동이력 조회
    private List<ActivityHistoryDto> getActivityHistoryData(String dojangCode, String month) {
        // (activity_name, activity_type, activity_date, participant_count)
        List<Object[]> activityData = activityRepository.findActivitiesWithParticipantsByMonth(dojangCode, month);
        
        return activityData.stream()
                .map(row -> {
                    String eventName = (String) row[0];
                    String eventType = (String) row[1];
                    LocalDate eventDate = ((Date) row[2]).toLocalDate();
                    Integer participantCount = ((Number) row[3]).intValue();
                    
                    return ActivityHistoryDto.builder()
                            .eventName(eventName)
                            .eventType(eventType)
                            .eventDate(eventDate)
                            .participantCount(participantCount)
                            .build();
                })
                .collect(Collectors.toList());
    }
}