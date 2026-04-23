package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.activity.reqDto.CreateActivityReqDto;
import com.taekwondo.miwool.dto.activity.reqDto.UpdateActivityReqDto;
import com.taekwondo.miwool.dto.activity.respDto.*;
import com.taekwondo.miwool.entity.Activity;
import com.taekwondo.miwool.entity.Student;
import com.taekwondo.miwool.entity.StudentActivity;
import com.taekwondo.miwool.repository.ActivityRepository;
import com.taekwondo.miwool.repository.StudentRepository;
import com.taekwondo.miwool.repository.StudentActivityRepository;
import com.taekwondo.miwool.util.AgeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityService {
    
    private final ActivityRepository activityRepository;
    private final StudentActivityRepository studentActivityRepository;
    private final StudentRepository studentRepository;
    
    /**
     * 활동내역 조회 (필터링)
     * 조건: 활동명(코드/명), 활동유형, 활동기간
     */
    public ActivityListRespDto getActivityList(
            String activityCodeOrName,
            String activityType,
            LocalDate startDate,
            LocalDate endDate) {
        
        log.info("활동내역 조회 시작: activityCodeOrName={}, activityType={}, startDate={}, endDate={}", 
                activityCodeOrName, activityType, startDate, endDate);
        
        Sort sort = Sort.by(Sort.Direction.DESC, "activity_date");
        
        // Repository에서 Native Query로 조회
        List<Activity> activities = activityRepository.findActivitiesWithFilters(
                activityCodeOrName,
                activityType,
                startDate,
                endDate,
                sort
        );
        
        // DTO 변환
        List<ActivityListItemDto> activityList = activities.stream()
                .map(activity -> {
                    // 참가자 요약 생성 (홍길동 외 14명)
                    String participantSummary = generateParticipantSummary(activity.getActivityCode());
                    
                    return ActivityListItemDto.builder()
                            .activityCode(activity.getActivityCode())
                            .activityDate(activity.getActivityDate())
                            .activityType(activity.getActivityType())
                            .activityName(activity.getActivityName())
                            .participantSummary(participantSummary)
                            .build();
                })
                .collect(Collectors.toList());
        
        log.info("활동내역 조회 완료: 조회 건수={}", activities.size());
        
        return ActivityListRespDto.builder()
        	    .activities(activityList)
        	    .build();
    }
    
    /**
     * 참가자 요약 생성 (홍길동 외 14명)
     */
    private String generateParticipantSummary(String activityCode) {
        // student_activity_code 순으로 정렬하여 첫 번째 제자 조회
        List<StudentActivity> participants = studentActivityRepository
                .findByActivityCodeOrderByStudentActivityCodeAsc(activityCode);
        
        if (participants.isEmpty()) {
            return "참가자 없음";
        }
        
        // 첫 번째 제자 이름 조회
        String firstStudentCode = participants.get(0).getStudentCode();
        Student firstStudent = studentRepository.findById(firstStudentCode)
                .orElse(null);
        
        String firstStudentName = (firstStudent != null) ? firstStudent.getStudentName() : "알 수 없음";
        
        int totalCount = participants.size();
        
        if (totalCount == 1) {
            return firstStudentName;
        } else {
            return String.format("%s 외 %d명", firstStudentName, totalCount - 1);
        }
    }
    
    /**
     * 활동 상세 조회
     */
    public ActivityDetailRespDto getActivityDetail(String activityCode) {
        
        log.info("활동 상세 조회 시작: activityCode={}", activityCode);
        
        // 1. 활동 조회
        Activity activity = activityRepository.findById(activityCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 활동입니다: " + activityCode));
        
        // 2. 참가자 목록 조회
        List<StudentActivity> studentActivities = studentActivityRepository
                .findByActivityCodeOrderByStudentActivityCodeAsc(activityCode);
        
        List<ActivityDetailRespDto.ParticipantDto> participants = studentActivities.stream()
                .map((StudentActivity sa) -> {
                    Student student = studentRepository.findById(sa.getStudentCode())
                            .orElse(null);
                    
                    if (student == null) {
                        return null;
                    }
                    
                    // 한국나이 계산
                    int age = AgeUtil.calculateKoreanAge(student.getBirthDate());
                    
                    return ActivityDetailRespDto.ParticipantDto.builder()
                            .studentCode(student.getStudentCode())
                            .studentName(student.getStudentName())
                            .beltCode(student.getBeltCode())
                            .genderCode(student.getGenderCode())
                            .grade(student.getGrade())
                            .age(age)
                            .build();
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());
        
        log.info("활동 상세 조회 완료: activityCode={}, 참가자 수={}", activityCode, participants.size());
        
        return ActivityDetailRespDto.builder()
                .activityCode(activity.getActivityCode())
                .activityName(activity.getActivityName())
                .activityDate(activity.getActivityDate())
                .activityType(activity.getActivityType())
                .description(activity.getDescription())
                .participants(participants)
                .build();
    }
    
    /**
     * 활동 생성
     */
    @Transactional
    public void createActivity(CreateActivityReqDto reqDto, String dojangCode) {
        
        log.info("활동 생성 시작: activityName={}", reqDto.getActivityName());
        
        // 1. activityCode 자동 생성
        String activityCode = generateActivityCode(dojangCode);
        
        // 2. Activity 생성
        Activity activity = Activity.builder()
                .activityCode(activityCode)
                .dojangCode(dojangCode)
                .activityName(reqDto.getActivityName())
                .activityDate(reqDto.getActivityDate())
                .activityType(reqDto.getActivityType())
                .description(reqDto.getDescription())
                .build();
        
        activityRepository.save(activity);
        
        // 3. StudentActivity 생성 (각 제자별)
        for (String studentCode : reqDto.getStudentCodes()) {
            // 제자 존재 확인
            if (!studentRepository.existsById(studentCode)) {
                log.warn("존재하지 않는 제자 코드: {}", studentCode);
                continue;
            }
            
            String studentActivityCode = generateStudentActivityCode(dojangCode);
            
            StudentActivity studentActivity = StudentActivity.builder()
                    .studentActivityCode(studentActivityCode)
                    .activityCode(activityCode)
                    .studentCode(studentCode)
                    .build();
            
            studentActivityRepository.save(studentActivity);
        }
        
        log.info("활동 생성 완료: activityCode={}, 참가자 수={}", activityCode, reqDto.getStudentCodes().size());
    }
    
    /**
     * 활동 수정
     */
    @Transactional
    public void updateActivity(UpdateActivityReqDto reqDto) {
        
        log.info("활동 수정 시작: activityCode={}", reqDto.getActivityCode());
        
        // 1. 활동 존재 확인
        Activity activity = activityRepository.findById(reqDto.getActivityCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 활동입니다: " + reqDto.getActivityCode()));
        
        // 2. 활동 정보 수정
        activity.setActivityName(reqDto.getActivityName());
        activity.setActivityDate(reqDto.getActivityDate());
        activity.setActivityType(reqDto.getActivityType());
        activity.setDescription(reqDto.getDescription());
        
        activityRepository.save(activity);
        
        // 3. 기존 참가자 전체 삭제
        studentActivityRepository.deleteByActivityCode(reqDto.getActivityCode());
        
        // 4. 새 참가자 등록
        String dojangCode = activity.getDojangCode();
        for (String studentCode : reqDto.getStudentCodes()) {
            // 제자 존재 확인
            if (!studentRepository.existsById(studentCode)) {
                log.warn("존재하지 않는 제자 코드: {}", studentCode);
                continue;
            }
            
            String studentActivityCode = generateStudentActivityCode(dojangCode);
            
            StudentActivity studentActivity = StudentActivity.builder()
                    .studentActivityCode(studentActivityCode)
                    .activityCode(reqDto.getActivityCode())
                    .studentCode(studentCode)
                    .build();
            
            studentActivityRepository.save(studentActivity);
        }
        
        log.info("활동 수정 완료: activityCode={}", reqDto.getActivityCode());
    }
    
    /**
     * 활동 삭제
     */
    @Transactional
    public void deleteActivity(String activityCode) {
        
        log.info("활동 삭제 시작: activityCode={}", activityCode);
        
        // 1. 활동 존재 확인
        Activity activity = activityRepository.findById(activityCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 활동입니다: " + activityCode));
        
        // 2. 참가자 전체 삭제
        studentActivityRepository.deleteByActivityCode(activityCode);
        
        // 3. 활동 삭제
        activityRepository.delete(activity);
        
        log.info("활동 삭제 완료: activityCode={}", activityCode);
    }
    
    /**
     * 제자 활동 목록 조회
     */
    public StudentActivityListRespDto getStudentActivityList(String studentCode, int page, int size) {
        
        log.info("제자 활동 목록 조회 시작: studentCode={}", studentCode);
        
        // 1. 제자 존재 확인
        if (!studentRepository.existsById(studentCode)) {
            throw new IllegalArgumentException("존재하지 않는 제자입니다: " + studentCode);
        }
        
        // 2. 페이징 설정 (최신순: activityDate DESC)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "activityDate"));
        
        // 3. 제자가 참여한 활동 조회
        Page<Activity> activityPage = activityRepository.findActivitiesByStudentCode(studentCode, pageable);
        
        // 4. DTO 변환
        List<StudentActivityListItemDto> activities = activityPage.getContent().stream()
                .map(activity -> StudentActivityListItemDto.builder()
                        .activityCode(activity.getActivityCode())
                        .activityType(activity.getActivityType())
                        .activityName(activity.getActivityName())
                        .activityDate(activity.getActivityDate())
                        .build())
                .collect(Collectors.toList());
        
        log.info("제자 활동 목록 조회 완료: studentCode={}, 조회 건수={}", studentCode, activities.size());
        
        return StudentActivityListRespDto.builder()
                .activities(activities)
                .totalPages(activityPage.getTotalPages())
                .totalElements(activityPage.getTotalElements())
                .currentPage(page)
                .size(size)
                .build();
    }
    
    /**
     * 제자 활동 상세 조회
     */
    public StudentActivityDetailRespDto getStudentActivityDetail(String studentCode, String activityCode) {
        
        log.info("제자 활동 상세 조회 시작: studentCode={}, activityCode={}", studentCode, activityCode);
        
        // 1. 제자 존재 확인
        if (!studentRepository.existsById(studentCode)) {
            throw new IllegalArgumentException("존재하지 않는 제자입니다: " + studentCode);
        }
        
        // 2. 활동 조회
        Activity activity = activityRepository.findById(activityCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 활동입니다: " + activityCode));
        
        // 3. 제자가 해당 활동에 참가했는지 확인
        boolean isParticipant = studentActivityRepository.existsByActivityCodeAndStudentCode(activityCode, studentCode);
        
        if (!isParticipant) {
            throw new IllegalArgumentException("해당 제자가 참가하지 않은 활동입니다");
        }
        
        log.info("제자 활동 상세 조회 완료: studentCode={}, activityCode={}", studentCode, activityCode);
        
        return StudentActivityDetailRespDto.builder()
                .activityCode(activity.getActivityCode())
                .activityDate(activity.getActivityDate())
                .activityType(activity.getActivityType())
                .activityName(activity.getActivityName())
                .description(activity.getDescription())
                .build();
    }
    
    /**
     * activityCode 자동 생성 (도장코드-AYYnnn)
     * 예: MW26001-A26001
     */
    private String generateActivityCode(String dojangCode) {
        String year = String.valueOf(LocalDate.now().getYear()).substring(2);
        String prefix = dojangCode + "-A" + year;

        return activityRepository.findFirstByActivityCodeStartingWithOrderByActivityCodeDesc(prefix)
                .map(activity -> {
                    String lastCode = activity.getActivityCode();
                    int nextSeq = Integer.parseInt(lastCode.substring(lastCode.length() - 3)) + 1;
                    return prefix + String.format("%03d", nextSeq);
                })
                .orElse(prefix + "001");
    }
    
    /**
     * studentActivityCode 자동 생성 (도장코드-SAYYnnn)
     * 예: MW26001-SA26001
     */
    private String generateStudentActivityCode(String dojangCode) {
        String year = String.valueOf(LocalDate.now().getYear()).substring(2);
        String prefix = dojangCode + "-SA" + year;

        return studentActivityRepository.findFirstByStudentActivityCodeStartingWithOrderByStudentActivityCodeDesc(prefix)
                .map(sa -> {
                    String lastCode = sa.getStudentActivityCode();
                    int nextSeq = Integer.parseInt(lastCode.substring(lastCode.length() - 3)) + 1;
                    return prefix + String.format("%03d", nextSeq);
                })
                .orElse(prefix + "001");
    }
}