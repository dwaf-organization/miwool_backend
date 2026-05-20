package com.taekwondo.miwool.service.admin;

import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedEducationRespDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedEducationRespDto.DojangEducationDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedEducationRespDto.ItemRateDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedEducationRespDto.LowestDojangDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedEducationRespDto.LowestItemDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedEducationRespDto.MonthlyEducationDto;
import com.taekwondo.miwool.dto.admin.integrated.respDto.IntegratedEducationRespDto.OverallEducationDto;
import com.taekwondo.miwool.repository.StudentManagementRepository;
import com.taekwondo.miwool.repository.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdminIntegratedEducationService {
    
    @Autowired
    private StudentManagementRepository studentManagementRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    // 교육지도 항목 8개
    private static final List<String> EDUCATION_ITEMS = Arrays.asList(
        "전화", "문자", "손편지", "간식", "상장", "영상", "관찰지", "기타"
    );
    
    @Transactional(readOnly = true)
    public IntegratedEducationRespDto getIntegratedEducation(String month) {
        log.info("교육지도관리 통합 데이터 조회 시작: month={}", month);
        
        // 1. 전체교육지도 현황
        OverallEducationDto overallEducation = getOverallEducation(month);
        
        // 2. 월별 실행률 추이 (3개월)
        List<MonthlyEducationDto> monthlyTrend = getMonthlyTrend(month);
        
        // 3. 도장별 교육지도 실행률
        List<DojangEducationDto> dojangEducation = getDojangEducation(month);
        
        // 4. 항목별 최저 실행률 도장 (각 2개)
        List<LowestItemDto> lowestByItem = getLowestByItem(month);
        
        log.info("교육지도관리 통합 데이터 조회 완료: month={}", month);
        
        return IntegratedEducationRespDto.builder()
                .overallEducation(overallEducation)
                .monthlyTrend(monthlyTrend)
                .dojangEducation(dojangEducation)
                .lowestByItem(lowestByItem)
                .build();
    }
    
    // 1. 전체교육지도 현황
    private OverallEducationDto getOverallEducation(String month) {
        // 전체 재원생 수
        int totalStudents = studentRepository.countByStatusCode("재원");
        
        // 항목별 실행 학생 수
        List<Object[]> itemData = studentManagementRepository.getItemExecutionByMonth(month);
        
        Map<String, Integer> executedMap = new HashMap<>();
        for (Object[] row : itemData) {
            String itemName = (String) row[0];
            Integer executedStudents = ((Number) row[1]).intValue();
            executedMap.put(itemName, executedStudents);
        }
        
        // 항목별 실행률 계산
        List<ItemRateDto> itemRates = EDUCATION_ITEMS.stream()
                .map(item -> {
                    int executed = executedMap.getOrDefault(item, 0);
                    double rate = totalStudents > 0 ? (executed * 100.0 / totalStudents) : 0.0;
                    rate = Math.round(rate * 10.0) / 10.0;
                    
                    return ItemRateDto.builder()
                            .itemName(item)
                            .executionRate(rate)
                            .build();
                })
                .collect(Collectors.toList());
        
        // 종합실행률 (평균)
        double overallRate = itemRates.stream()
                .mapToDouble(ItemRateDto::getExecutionRate)
                .average()
                .orElse(0.0);
        overallRate = Math.round(overallRate * 10.0) / 10.0;
        
        return OverallEducationDto.builder()
                .overallExecutionRate(overallRate)
                .itemExecutionRates(itemRates)
                .build();
    }
    
    // 2. 월별 실행률 추이 (3개월)
    private List<MonthlyEducationDto> getMonthlyTrend(String month) {
        // 전체 재원생 수
        int totalStudents = studentRepository.countByStatusCode("재원");
        
        // (month, management_type_code, executed_students)
        List<Object[]> trendData = studentManagementRepository.getMonthlyItemExecutionTrend(month);
        
        // Map: month -> (itemName -> executedStudents)
        Map<String, Map<String, Integer>> monthItemMap = new HashMap<>();
        for (Object[] row : trendData) {
            String monthStr = (String) row[0];
            String itemName = (String) row[1];
            Integer executedStudents = ((Number) row[2]).intValue();
            
            monthItemMap.putIfAbsent(monthStr, new HashMap<>());
            monthItemMap.get(monthStr).put(itemName, executedStudents);
        }
        
        // 3개월 생성
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate endDate = LocalDate.parse(month + "-01");
        LocalDate startDate = endDate.minusMonths(2);
        
        List<MonthlyEducationDto> result = new ArrayList<>();
        LocalDate current = startDate;
        
        while (!current.isAfter(endDate)) {
            String monthStr = current.format(formatter);
            Map<String, Integer> itemMap = monthItemMap.getOrDefault(monthStr, new HashMap<>());
            
            List<ItemRateDto> itemRates = EDUCATION_ITEMS.stream()
                    .map(item -> {
                        int executed = itemMap.getOrDefault(item, 0);
                        double rate = totalStudents > 0 ? (executed * 100.0 / totalStudents) : 0.0;
                        rate = Math.round(rate * 10.0) / 10.0;
                        
                        return ItemRateDto.builder()
                                .itemName(item)
                                .executionRate(rate)
                                .build();
                    })
                    .collect(Collectors.toList());
            
            result.add(MonthlyEducationDto.builder()
                    .month(monthStr)
                    .itemRates(itemRates)
                    .build());
            
            current = current.plusMonths(1);
        }
        
        return result;
    }
    
    // 3. 도장별 교육지도 실행률
    private List<DojangEducationDto> getDojangEducation(String month) {
        // (dojang_code, dojang_name, total_students, management_type_code, executed_students)
        List<Object[]> dojangData = studentManagementRepository.getDojangItemExecution(month);
        
        // Map: dojang_code -> (dojang_name, total_students, itemMap)
        Map<String, DojangInfo> dojangMap = new HashMap<>();
        
        for (Object[] row : dojangData) {
            String dojangCode = (String) row[0];
            String dojangName = (String) row[1];
            Integer totalStudents = ((Number) row[2]).intValue();
            String itemName = (String) row[3];
            Integer executedStudents = row[4] != null ? ((Number) row[4]).intValue() : 0;
            
            dojangMap.putIfAbsent(dojangCode, new DojangInfo(dojangName, totalStudents));
            if (itemName != null) {
                dojangMap.get(dojangCode).itemMap.put(itemName, executedStudents);
            }
        }
        
        return dojangMap.values().stream()
                .map(info -> {
                    List<ItemRateDto> itemRates = EDUCATION_ITEMS.stream()
                            .map(item -> {
                                int executed = info.itemMap.getOrDefault(item, 0);
                                double rate = info.totalStudents > 0 ? 
                                        (executed * 100.0 / info.totalStudents) : 0.0;
                                rate = Math.round(rate * 10.0) / 10.0;
                                
                                return ItemRateDto.builder()
                                        .itemName(item)
                                        .executionRate(rate)
                                        .build();
                            })
                            .collect(Collectors.toList());
                    
                    return DojangEducationDto.builder()
                            .dojangName(info.dojangName)
                            .totalStudents(info.totalStudents)
                            .itemRates(itemRates)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    // 4. 항목별 최저 실행률 도장 (각 2개)
    private List<LowestItemDto> getLowestByItem(String month) {
        // (management_type_code, dojang_code, dojang_name, total_students, executed_students)
        List<Object[]> rateData = studentManagementRepository.getItemDojangExecutionRates(month);
        
        // Map: itemName -> List<(dojangName, rate)>
        Map<String, List<DojangRate>> itemDojangMap = new HashMap<>();
        
        for (Object[] row : rateData) {
            String itemName = (String) row[0];
            String dojangName = (String) row[2];
            Integer totalStudents = ((Number) row[3]).intValue();
            Integer executedStudents = ((Number) row[4]).intValue();
            
            double rate = totalStudents > 0 ? (executedStudents * 100.0 / totalStudents) : 0.0;
            rate = Math.round(rate * 10.0) / 10.0;
            
            itemDojangMap.putIfAbsent(itemName, new ArrayList<>());
            itemDojangMap.get(itemName).add(new DojangRate(dojangName, rate));
        }
        
        return EDUCATION_ITEMS.stream()
                .map(item -> {
                    List<DojangRate> dojangRates = itemDojangMap.getOrDefault(item, new ArrayList<>());
                    
                    // 실행률 낮은 순으로 정렬 후 2개
                    List<LowestDojangDto> lowestDojangs = dojangRates.stream()
                            .sorted(Comparator.comparing(DojangRate::getRate))
                            .limit(2)
                            .map(dr -> LowestDojangDto.builder()
                                    .dojangName(dr.dojangName)
                                    .executionRate(dr.rate)
                                    .build())
                            .collect(Collectors.toList());
                    
                    return LowestItemDto.builder()
                            .itemName(item)
                            .lowestDojangs(lowestDojangs)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    // 내부 클래스
    private static class DojangInfo {
        String dojangName;
        Integer totalStudents;
        Map<String, Integer> itemMap = new HashMap<>();
        
        DojangInfo(String dojangName, Integer totalStudents) {
            this.dojangName = dojangName;
            this.totalStudents = totalStudents;
        }
    }
    
    private static class DojangRate {
        String dojangName;
        Double rate;
        
        DojangRate(String dojangName, Double rate) {
            this.dojangName = dojangName;
            this.rate = rate;
        }
        
        Double getRate() {
            return rate;
        }
    }
}