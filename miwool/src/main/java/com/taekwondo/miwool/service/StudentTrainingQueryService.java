package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.training.respDto.StudentTrainingDetailRespDto;
import com.taekwondo.miwool.dto.training.respDto.StudentTrainingInfoRespDto;
import com.taekwondo.miwool.dto.training.respDto.TrainingClassDto;
import com.taekwondo.miwool.entity.*;
import com.taekwondo.miwool.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentTrainingQueryService {
    
    private final StudentTrainingRepository studentTrainingRepository;
    private final StudentTuitionRepository studentTuitionRepository;
    private final StudentClassRepository studentClassRepository;
    private final TrainingMstRepository trainingMstRepository;
    private final ClassMstRepository classMstRepository;
    
    /**
     * 학생별 수련정보 목록 조회
     */
    @Transactional(readOnly = true)
    public List<StudentTrainingInfoRespDto> getStudentTrainingList(String studentCode, String dojangCode) {
        log.info("학생별 수련정보 목록 조회: studentCode={}", studentCode);
        
        // 학생의 모든 수련정보 조회
        List<StudentTraining> trainings = studentTrainingRepository.findByStudentCode(studentCode);
        
        List<StudentTrainingInfoRespDto> result = new ArrayList<>();
        
        for (StudentTraining training : trainings) {
            Integer trainingInfoCode = training.getTrainingInfoCode();
            
            // 교육비 정보 조회
            StudentTuition tuition = studentTuitionRepository.findByTrainingInfoCode(trainingInfoCode)
                    .orElse(null);
            
            if (tuition == null) continue;
            
            // 패키지 정보 조회
            TrainingMst packageInfo = trainingMstRepository.findById(training.getPackageCode())
                    .orElse(null);
            
            if (packageInfo == null) continue;
            
            // 수업 목록 조회
            List<StudentClass> studentClasses = studentClassRepository
                    .findByTrainingInfoCode(trainingInfoCode);
            
            List<TrainingClassDto> classes = new ArrayList<>();
            
            for (StudentClass sc : studentClasses) {
                ClassMst classMst = classMstRepository.findById(sc.getClassCode()).orElse(null);
                
                if (classMst != null) {
                    String classTime = formatClassTime(classMst.getStartTime(), classMst.getEndTime());
                    
                    TrainingClassDto classDto = TrainingClassDto.builder()
                            .classCode(classMst.getClassCode())
                            .className(classMst.getClassName())
                            .dayOfWeek(classMst.getDayOfWeek())
                            .classTime(classTime)
                            .build();
                    
                    classes.add(classDto);
                }
            }
            
            // 응답 생성
            StudentTrainingInfoRespDto dto = StudentTrainingInfoRespDto.builder()
                    .trainingInfoCode(trainingInfoCode)
                    .packageCode(training.getPackageCode())
                    .packageName(packageInfo.getPackageName())
                    .basePrice(tuition.getBasePrice())
                    .adjustmentAmount(tuition.getAdjustmentAmount())
                    .adjustmentDetail(tuition.getAdjustmentDetail())
                    .actualPrice(tuition.getActualPrice())
                    .trainingStartDate(training.getStartDate())
                    .classes(classes)
                    .useVehicle(training.getUseVehicle())
                    .pickupLocation(training.getPickupLocation())
                    .dropoffLocation(training.getDropoffLocation())
                    .handoverMethod(training.getHandoverMethod())
                    .build();
            
            result.add(dto);
        }
        
        log.info("수련정보 목록 조회 완료: count={}", result.size());
        return result;
    }
    
    /**
     * 수련정보 상세 조회
     */
    @Transactional(readOnly = true)
    public StudentTrainingDetailRespDto getStudentTrainingDetail(
            String studentCode, Integer trainingInfoCode, String dojangCode) {
        log.info("수련정보 상세 조회: studentCode={}, trainingInfoCode={}", studentCode, trainingInfoCode);
        
        // 수련정보 조회
        StudentTraining training = studentTrainingRepository.findById(trainingInfoCode)
                .orElseThrow(() -> new IllegalArgumentException("수련정보를 찾을 수 없습니다: " + trainingInfoCode));
        
        // 본인 학생인지 확인
        if (!training.getStudentCode().equals(studentCode)) {
            throw new IllegalArgumentException("해당 수련정보에 대한 권한이 없습니다.");
        }
        
        // 교육비 정보 조회
        StudentTuition tuition = studentTuitionRepository.findByTrainingInfoCode(trainingInfoCode)
                .orElseThrow(() -> new IllegalArgumentException("교육비정보를 찾을 수 없습니다"));
        
        // 수업 목록 조회
        List<StudentClass> studentClasses = studentClassRepository
                .findByTrainingInfoCode(trainingInfoCode);
        
        List<String> classCodes = studentClasses.stream()
                .map(StudentClass::getClassCode)
                .collect(Collectors.toList());
        
        // 응답 생성
        StudentTrainingDetailRespDto dto = StudentTrainingDetailRespDto.builder()
                .trainingInfoCode(trainingInfoCode)
                .studentCode(training.getStudentCode())
                .packageCode(training.getPackageCode())
                .classCodes(classCodes)
                .useVehicle(training.getUseVehicle())
                .pickupLocation(training.getPickupLocation())
                .dropoffLocation(training.getDropoffLocation())
                .handoverMethod(training.getHandoverMethod())
                .adjustmentAmount(tuition.getAdjustmentAmount())
                .adjustmentDetail(tuition.getAdjustmentDetail())
                .trainingStartDate(training.getStartDate())
                .build();
        
        log.info("수련정보 상세 조회 완료");
        return dto;
    }
    
    /**
     * 수업시간 포맷팅
     */
    private String formatClassTime(java.time.LocalTime startTime, java.time.LocalTime endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return startTime.format(formatter) + "~" + endTime.format(formatter);
    }
}