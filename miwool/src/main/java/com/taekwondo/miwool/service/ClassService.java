package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.training.reqDto.CreateClassReqDto;
import com.taekwondo.miwool.dto.training.reqDto.UpdateClassReqDto;
import com.taekwondo.miwool.dto.training.respDto.ClassDetailRespDto;
import com.taekwondo.miwool.dto.training.respDto.ClassOptionRespDto;
import com.taekwondo.miwool.dto.training.respDto.ClassRespDto;
import com.taekwondo.miwool.dto.training.respDto.ClassStudentDto;
import com.taekwondo.miwool.entity.ClassMst;
import com.taekwondo.miwool.entity.CommonCode;
import com.taekwondo.miwool.entity.Student;
import com.taekwondo.miwool.entity.StudentClass;
import com.taekwondo.miwool.entity.StudentTraining;
import com.taekwondo.miwool.repository.ClassMstRepository;
import com.taekwondo.miwool.repository.CommonCodeRepository;
import com.taekwondo.miwool.repository.StudentClassRepository;
import com.taekwondo.miwool.repository.StudentRepository;
import com.taekwondo.miwool.repository.StudentTrainingRepository;
import com.taekwondo.miwool.util.AgeUtil;
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
public class ClassService {
    
    private final ClassMstRepository classMstRepository;
    private final StudentClassRepository studentClassRepository;
    private final StudentRepository studentMstRepository;
    private final StudentTrainingRepository studentTrainingRepository;
    private final CommonCodeRepository commonMstRepository;
    
    /**
     * 수업 생성
     */
    @Transactional
    public void createClass(CreateClassReqDto reqDto, String dojangCode) {
        log.info("수업 생성: dojangCode={}, className={}", dojangCode, reqDto.getClassName());
        
        // classCode 자동 생성 (CLS001, CLS002, ...)
        String classCode = generateClassCode(dojangCode);
        
        ClassMst classMst = ClassMst.builder()
                .classCode(classCode)
                .dojangCode(dojangCode)
                .className(reqDto.getClassName())
                .dayOfWeek(reqDto.getDayOfWeek())
                .startTime(reqDto.getStartTime())
                .endTime(reqDto.getEndTime())
                .description(reqDto.getDescription())
                .useYn("Y")  // 기본값 Y
                .build();
        
        classMstRepository.save(classMst);
        
        log.info("수업 생성 완료: classCode={}", classCode);
    }
    
    /**
     * 수업 목록 조회 (최신순)
     */
    public List<ClassRespDto> getClassList(String dojangCode) {
        log.info("수업 목록 조회: dojangCode={}", dojangCode);
        
        List<ClassMst> classes = classMstRepository
                .findByDojangCodeOrderByCreatedAtDesc(dojangCode);
        
        return classes.stream()
                .map(cls -> ClassRespDto.builder()
                        .classCode(cls.getClassCode())
                        .className(cls.getClassName())
                        .dayOfWeek(cls.getDayOfWeek())
                        .classTime(formatClassTime(cls.getStartTime(), cls.getEndTime()))
                        .description(cls.getDescription())
                        .useYn(cls.getUseYn())
                        .createdAt(cls.getCreatedAt())
                        .updatedAt(cls.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 수업 선택용 목록 조회
     * "수업명 수업시간(11:00 ~ 13:00)"
     */
    public List<ClassOptionRespDto> getClassOptions(String dojangCode) {
        log.info("수업 선택용 목록 조회: dojangCode={}", dojangCode);
        
        List<ClassMst> classes = classMstRepository
                .findByDojangCodeOrderByCreatedAtDesc(dojangCode);
        
        return classes.stream()
                .map(cls -> {
                    String classTime = formatClassTime(cls.getStartTime(), cls.getEndTime());
                    String displayText = String.format("%s %s",
                            cls.getClassName(),
                            classTime);
                    
                    return ClassOptionRespDto.builder()
                            .classCode(cls.getClassCode())
                            .displayText(displayText)
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 수업 상세 조회 (학생 목록 포함)
     */
    public ClassDetailRespDto getClassDetail(String classCode, String dojangCode) {
        log.info("수업 상세 조회: classCode={}", classCode);
        
        // 1. 수업 기본정보 조회
        ClassMst classMst = classMstRepository.findById(classCode)
                .orElseThrow(() -> new IllegalArgumentException("수업을 찾을 수 없습니다: " + classCode));
        
        // 본인 도장 수업인지 확인
        if (!classMst.getDojangCode().equals(dojangCode)) {
            throw new IllegalArgumentException("조회 권한이 없습니다.");
        }
        
        // 2. 해당 수업의 학생 목록 조회
        List<StudentClass> studentClasses = studentClassRepository
                .findByClassCode(classCode);
        
        List<ClassStudentDto> students = new ArrayList<>();
        
        for (StudentClass sc : studentClasses) {
            String studentCode = sc.getStudentCode();
            Integer trainingInfoCode = sc.getTrainingInfoCode();
            
            // 학생 기본정보 조회
            Student student = studentMstRepository.findById(studentCode).orElse(null);
            if (student == null) continue;
            
            // 학생 수련정보 조회 (training_info_code로 직접 조회)
            StudentTraining training = studentTrainingRepository
                    .findById(trainingInfoCode).orElse(null);
            
            // 급수명 조회
            String beltName = getBeltName(student.getBeltCode());
            
            ClassStudentDto studentDto = ClassStudentDto.builder()
                    .studentCode(student.getStudentCode())
                    .genderCode(student.getGenderCode())
                    .studentName(student.getStudentName())
                    .age(AgeUtil.calculateKoreanAge(student.getBirthDate()))
                    .grade(student.getGrade())
                    .beltCode(student.getBeltCode())
                    .beltName(beltName)
                    .useVehicle(training != null ? training.getUseVehicle() : 0)
                    .pickupLocation(training != null ? training.getPickupLocation() : "")
                    .dropoffLocation(training != null ? training.getDropoffLocation() : "")
                    .handoverMethod(training != null ? training.getHandoverMethod() : "")
                    .build();
            
            students.add(studentDto);
        }
        
        return ClassDetailRespDto.builder()
                .classCode(classMst.getClassCode())
                .className(classMst.getClassName())
                .dayOfWeek(classMst.getDayOfWeek())
                .classTime(formatClassTime(classMst.getStartTime(), classMst.getEndTime()))
                .description(classMst.getDescription())
                .useYn(classMst.getUseYn())
                .createdAt(classMst.getCreatedAt())
                .updatedAt(classMst.getUpdatedAt())
                .students(students)
                .build();
    }
    
    /**
     * 수업 수정
     */
    @Transactional
    public void updateClass(UpdateClassReqDto reqDto, String dojangCode) {
        log.info("수업 수정: classCode={}", reqDto.getClassCode());
        
        ClassMst classMst = classMstRepository.findById(reqDto.getClassCode())
                .orElseThrow(() -> new IllegalArgumentException("수업을 찾을 수 없습니다: " + reqDto.getClassCode()));
        
        // 본인 도장 수업인지 확인
        if (!classMst.getDojangCode().equals(dojangCode)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        
        // 수정
        classMst.setClassName(reqDto.getClassName());
        classMst.setDayOfWeek(reqDto.getDayOfWeek());
        classMst.setStartTime(reqDto.getStartTime());
        classMst.setEndTime(reqDto.getEndTime());
        classMst.setDescription(reqDto.getDescription());
        classMst.setUseYn(reqDto.getUseYn());
        
        classMstRepository.save(classMst);
        
        log.info("수업 수정 완료: classCode={}", reqDto.getClassCode());
    }
    
    /**
     * 수업 삭제 (하드 딜리트)
     */
    @Transactional
    public void deleteClass(String classCode, String dojangCode) {
        log.info("수업 삭제: classCode={}", classCode);
        
        ClassMst classMst = classMstRepository.findById(classCode)
                .orElseThrow(() -> new IllegalArgumentException("수업을 찾을 수 없습니다: " + classCode));
        
        // 본인 도장 수업인지 확인
        if (!classMst.getDojangCode().equals(dojangCode)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        
        classMstRepository.delete(classMst);
        
        log.info("수업 삭제 완료: classCode={}", classCode);
    }
    
    /**
     * classCode 자동 생성
     * 형식: {dojangCode}-CLS001
     */
    private String generateClassCode(String dojangCode) {
        String prefix = dojangCode + "-CLS";
        
        return classMstRepository.findFirstByClassCodeStartingWithOrderByClassCodeDesc(prefix)
                .map(entity -> {
                    String lastCode = entity.getClassCode();
                    int nextSeq = Integer.parseInt(lastCode.substring(lastCode.length() - 3)) + 1;
                    return prefix + String.format("%03d", nextSeq);
                })
                .orElse(prefix + "001");
    }
    
    /**
     * 수업시간 포맷팅
     * "11:00 ~ 13:00"
     */
    private String formatClassTime(java.time.LocalTime startTime, java.time.LocalTime endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return startTime.format(formatter) + "~" + endTime.format(formatter);
    }
    
    /**
     * 급수명 조회
     */
    private String getBeltName(String beltCode) {
        if (beltCode == null || beltCode.isEmpty()) {
            return "";
        }
        
        return commonMstRepository.findById(beltCode)
                .map(CommonCode::getCodeName)
                .orElse("");
    }
}