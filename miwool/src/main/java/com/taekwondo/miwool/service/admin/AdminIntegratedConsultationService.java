package com.taekwondo.miwool.service.admin;

import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedConsultationRespDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedConsultationRespDto.CounselKpiDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedConsultationRespDto.CounselTrendDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedConsultationRespDto.CounselTypeDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedConsultationRespDto.DojangCounselDto;
import com.taekwondo.miwool.repository.StudentCounselRepository;
import com.taekwondo.miwool.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminIntegratedConsultationService {
    
    @Autowired
    private StudentCounselRepository studentCounselRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Transactional(readOnly = true)
    public IntegratedConsultationRespDto getIntegratedConsultation(String month) {
        log.info("상담관리 통합 데이터 조회 시작: month={}", month);
        
        // 1. 상담 KPI 카드
        CounselKpiDto counselKpi = getCounselKpi(month);
        
        // 2. 상담실행률 추이 (12개월)
        List<CounselTrendDto> counselTrend = getCounselTrend(month);
        
        // 3. 도장별 상담현황
        List<DojangCounselDto> dojangCounselStatus = getDojangCounselStatus(month);
        
        // 4. 상담항목별 현황
        List<CounselTypeDto> counselTypeStatistics = getCounselTypeStatistics(month);
        
        log.info("상담관리 통합 데이터 조회 완료: month={}", month);
        
        return IntegratedConsultationRespDto.builder()
                .counselKpi(counselKpi)
                .counselTrend(counselTrend)
                .dojangCounselStatus(dojangCounselStatus)
                .counselTypeStatistics(counselTypeStatistics)
                .build();
    }
    
    // 1. 상담 KPI 카드
    private CounselKpiDto getCounselKpi(String month) {
        // 총 상담건수
        int totalCounselCount = studentCounselRepository.getTotalCounselCount(month);
        
        // 총 상담제자수 (고유 학생)
        int totalCounseledStudents = studentCounselRepository.getTotalCounseledStudents(month);
        
        // 전체 재원생 수
        int totalStudents = studentRepository.countByStatusCode("재원");
        
        // 미상담제자수
        int notCounseledStudents = totalStudents - totalCounseledStudents;
        if (notCounseledStudents < 0) notCounseledStudents = 0;
        
        return CounselKpiDto.builder()
                .totalCounselCount(totalCounselCount)
                .totalCounseledStudents(totalCounseledStudents)
                .notCounseledStudents(notCounseledStudents)
                .build();
    }
    
    // 2. 상담실행률 추이 (12개월)
    private List<CounselTrendDto> getCounselTrend(String month) {
        // (month, counsel_count, counseled_students)
        List<Object[]> trendData = studentCounselRepository.getMonthlyCounselTrend(month);
        
        // Map으로 변환
        Map<String, Object[]> trendMap = new HashMap<>();
        for (Object[] row : trendData) {
            String monthStr = (String) row[0];
            trendMap.put(monthStr, row);
        }
        
        // 12개월 전부터 현재까지 모든 월 생성
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate endDate = LocalDate.parse(month + "-01");
        LocalDate startDate = endDate.minusMonths(11);
        
        List<CounselTrendDto> result = new ArrayList<>();
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            String monthStr = current.format(formatter);
            
            int counselCount = 0;
            int counseledStudents = 0;
            
            if (trendMap.containsKey(monthStr)) {
                Object[] row = trendMap.get(monthStr);
                counselCount = ((Number) row[1]).intValue();
                counseledStudents = ((Number) row[2]).intValue();
            }
            
            // 해당 월의 전체 재원생 수 (현재는 전체 재원생 수로 대체)
            int totalStudents = studentRepository.countByStatusCode("재원");
            
            // 실행율 = 상담제자수 / 전체제자수 × 100
            double executionRate = 0.0;
            if (totalStudents > 0) {
                executionRate = (counseledStudents * 100.0 / totalStudents);
                executionRate = Math.round(executionRate * 10.0) / 10.0;
            }
            
            result.add(CounselTrendDto.builder()
                    .month(monthStr)
                    .counselCount(counselCount)
                    .executionRate(executionRate)
                    .build());
            
            current = current.plusMonths(1);
        }
        
        return result;
    }
    
    // 3. 도장별 상담현황
    private List<DojangCounselDto> getDojangCounselStatus(String month) {
        // (dojang_code, dojang_name, total_students, counsel_count, counseled_students)
        List<Object[]> dojangData = studentCounselRepository.getDojangCounselStatus(month);
        
        List<DojangCounselDto> result = dojangData.stream()
                .map(row -> {
                    String dojangName = (String) row[1];
                    Integer totalStudents = ((Number) row[2]).intValue();
                    Integer counselCount = ((Number) row[3]).intValue();
                    Integer counseledStudents = ((Number) row[4]).intValue();
                    
                    // 실행율 = 상담제자수 / 전체제자수 × 100
                    double executionRate = 0.0;
                    if (totalStudents > 0) {
                        executionRate = (counseledStudents * 100.0 / totalStudents);
                        executionRate = Math.round(executionRate * 10.0) / 10.0;
                    }
                    
                    // 미상담제자수
                    int notCounseledStudents = totalStudents - counseledStudents;
                    if (notCounseledStudents < 0) notCounseledStudents = 0;
                    
                    return DojangCounselDto.builder()
                            .dojangName(dojangName)
                            .totalStudents(totalStudents)
                            .counselCount(counselCount)
                            .counseledStudents(counseledStudents)
                            .executionRate(executionRate)
                            .notCounseledStudents(notCounseledStudents)
                            .build();
                })
                .sorted(Comparator.comparing(DojangCounselDto::getExecutionRate).reversed()) // 실행율 내림차순
                .collect(Collectors.toList());
        
        return result;
    }
    
    // 4. 상담항목별 현황
    private List<CounselTypeDto> getCounselTypeStatistics(String month) {
        // (counsel_type, count)
        List<Object[]> typeData = studentCounselRepository.getCounselTypeStatistics(month);
        
        // Map으로 변환
        Map<String, Integer> typeCountMap = new HashMap<>();
        for (Object[] row : typeData) {
            String counselType = (String) row[0];
            Integer count = ((Number) row[1]).intValue();
            typeCountMap.put(counselType, count);
        }
        
        // 전체 상담건수
        int totalCount = typeCountMap.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        
        // 3개 상담 유형 고정 (없으면 0)
        List<String> counselTypes = Arrays.asList("대면상담", "전화상담", "문자상담");
        
        return counselTypes.stream()
                .map(type -> {
                    Integer count = typeCountMap.getOrDefault(type, 0);
                    
                    // 비율 계산
                    double percentage = 0.0;
                    if (totalCount > 0) {
                        percentage = (count * 100.0 / totalCount);
                        percentage = Math.round(percentage * 10.0) / 10.0;
                    }
                    
                    return CounselTypeDto.builder()
                            .counselType(type)
                            .count(count)
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
    }
}