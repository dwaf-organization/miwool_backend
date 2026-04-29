package com.taekwondo.miwool.service.app;

import com.taekwondo.miwool.dto.app.class_schedule.respDto.ClassDetailRespDto;
import com.taekwondo.miwool.dto.app.class_schedule.respDto.ClassStudentDetailDto;
import com.taekwondo.miwool.dto.app.class_schedule.respDto.DailyClassDto;
import com.taekwondo.miwool.dto.app.class_schedule.respDto.TodayClassItemDto;
import com.taekwondo.miwool.dto.app.class_schedule.respDto.TodayClassRespDto;
import com.taekwondo.miwool.entity.ClassMst;
import com.taekwondo.miwool.repository.ClassMstRepository;
import com.taekwondo.miwool.util.AgeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppClassService {

    private final ClassMstRepository classMstRepository;

    /**
     * 앱 오늘의 수업 조회 (오늘부터 4일간)
     */
    @Transactional(readOnly = true)
    public TodayClassRespDto getTodayClasses(String dojangCode) {
        log.info("앱 오늘의 수업 조회 (4일): dojangCode={}", dojangCode);
        
        List<DailyClassDto> days = new ArrayList<>();
        
        // 오늘부터 4일간 수업 조회
        for (int i = 0; i < 4; i++) {
            LocalDate targetDate = LocalDate.now().plusDays(i);
            String dayOfWeek = getDayOfWeekInKorean(targetDate);
            String dateStr = formatTodayDate(targetDate, dayOfWeek);
            
            log.info("날짜 조회: {}, 요일: {}", targetDate, dayOfWeek);
            
            // 해당 요일의 수업 조회
            List<Object[]> results = classMstRepository.findTodayClassesByDayOfWeek(dojangCode, dayOfWeek);
            
            // DTO 변환
            List<TodayClassItemDto> classes = results.stream()
                    .map(row -> {
                        String classCode = (String) row[0];
                        String className = (String) row[1];
                        LocalTime startTime = ((java.sql.Time) row[2]).toLocalTime();
                        LocalTime endTime = ((java.sql.Time) row[3]).toLocalTime();
                        Integer participantCount = ((Number) row[4]).intValue();
                        
                        String classTime = formatClassTime(startTime, endTime);
                        
                        return TodayClassItemDto.builder()
                                .classCode(classCode)
                                .classTime(classTime)
                                .className(className)
                                .participantCount(participantCount)
                                .build();
                    })
                    .collect(Collectors.toList());
            
            // 일별 데이터 추가
            days.add(DailyClassDto.builder()
                    .date(dateStr)
                    .classes(classes)
                    .build());
        }
        
        log.info("앱 오늘의 수업 조회 완료: 총 4일");
        
        return TodayClassRespDto.builder()
                .days(days)
                .build();
    }

    /**
     * 앱 수업 상세 조회
     */
    @Transactional(readOnly = true)
    public ClassDetailRespDto getClassDetail(String dojangCode, String classCode) {
        log.info("앱 수업 상세 조회: dojangCode={}, classCode={}", dojangCode, classCode);
        
        // 1. 수업 기본정보 조회
        ClassMst classMst = classMstRepository.findById(classCode)
                .orElseThrow(() -> new IllegalArgumentException("수업을 찾을 수 없습니다."));
        
        // 도장 권한 확인
        if (!classMst.getDojangCode().equals(dojangCode)) {
            throw new IllegalArgumentException("조회 권한이 없습니다.");
        }
        
        String className = classMst.getClassName();
        String dayOfWeek = classMst.getDayOfWeek();
        String classTime = formatClassTime(classMst.getStartTime(), classMst.getEndTime());
        
        // 2. 제자 목록 조회
        List<Object[]> results = classMstRepository.findClassStudentDetails(classCode);
        
        // 3. DTO 변환
        List<ClassStudentDetailDto> students = results.stream()
                .map(row -> {
                    Integer genderCode = (Integer) row[0];
                    String studentName = (String) row[1];
                    LocalDate birthDate = ((java.sql.Date) row[2]).toLocalDate();
                    String grade = (String) row[3];
                    String beltCode = (String) row[4];
                    String beltName = (String) row[5];
                    String guardianPhone = (String) row[6];
                    String studentPhone = (String) row[7];
                    
                    // useVehicle은 Boolean으로 반환될 수 있음
                    Integer useVehicle = (row[8] instanceof Boolean) 
                            ? ((Boolean) row[8] ? 1 : 0) 
                            : ((Number) row[8]).intValue();
                    
                    String pickupLocation = (String) row[9];
                    String dropoffLocation = (String) row[10];
                    String handoverMethod = (String) row[11];
                    
                    // 나이 계산
                    Integer age = AgeUtil.calculateKoreanAge(birthDate);
                    
                    // 차량이용 안 하면 차량 관련 필드 null 처리
                    if (useVehicle == 0) {
                        pickupLocation = null;
                        dropoffLocation = null;
                        handoverMethod = null;
                    }
                    
                    return ClassStudentDetailDto.builder()
                            .genderCode(genderCode)
                            .studentName(studentName)
                            .age(age)
                            .grade(grade)
                            .beltName(beltName)
                            .guardianPhone(guardianPhone)
                            .studentPhone(studentPhone)
                            .useVehicle(useVehicle)
                            .pickupLocation(pickupLocation)
                            .dropoffLocation(dropoffLocation)
                            .handoverMethod(handoverMethod)
                            .build();
                })
                .collect(Collectors.toList());
        
        log.info("앱 수업 상세 조회 완료: 제자 수={}", students.size());
        
        return ClassDetailRespDto.builder()
                .className(className)
                .dayOfWeek(dayOfWeek)
                .classTime(classTime)
                .students(students)
                .build();
    }

    /**
     * 요일을 한글로 변환
     */
    private String getDayOfWeekInKorean(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY: return "월요일";
            case TUESDAY: return "화요일";
            case WEDNESDAY: return "수요일";
            case THURSDAY: return "목요일";
            case FRIDAY: return "금요일";
            case SATURDAY: return "토요일";
            case SUNDAY: return "일요일";
            default: return "";
        }
    }

    /**
     * 오늘 날짜 포맷팅
     * "2026-04-27(월요일)"
     */
    private String formatTodayDate(LocalDate date, String dayOfWeek) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter) + "(" + dayOfWeek + ")";
    }

    /**
     * 수업시간 포맷팅
     * "10:00~12:00"
     */
    private String formatClassTime(LocalTime startTime, LocalTime endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return startTime.format(formatter) + "~" + endTime.format(formatter);
    }
}