package com.taekwondo.miwool.service.app;

import com.taekwondo.miwool.dto.app.training.reqDto.RegisterTrainingReqDto;
import com.taekwondo.miwool.dto.app.training.respDto.ClassOptionRespDto;
import com.taekwondo.miwool.dto.app.training.respDto.PackageOptionRespDto;
import com.taekwondo.miwool.entity.ClassMst;
import com.taekwondo.miwool.repository.ClassMstRepository;
import com.taekwondo.miwool.service.TrainingPackageService;
import com.taekwondo.miwool.service.TrainingRegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppTrainingService {

    private final TrainingPackageService trainingPackageService;
    private final ClassMstRepository classMstRepository;
    private final TrainingRegistrationService trainingRegistrationService;

    /**
     * 앱 패키지 선택 목록 조회
     */
    public List<PackageOptionRespDto> getPackageOptions(String dojangCode) {
        log.info("앱 패키지 선택 목록 조회: dojangCode={}", dojangCode);
        
        // 웹 서비스 메서드 재사용
        List<com.taekwondo.miwool.dto.training.respDto.PackageOptionRespDto> webOptions = 
                trainingPackageService.getPackageOptions(dojangCode);
        
        // 앱 DTO로 변환
        return webOptions.stream()
                .map(webDto -> PackageOptionRespDto.builder()
                        .packageCode(webDto.getPackageCode())
                        .displayText(webDto.getDisplayText())
                        .packageName(webDto.getPackageName())
                        .weeklyCount(webDto.getWeeklyCount())
                        .basePrice(webDto.getBasePrice())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 앱 수업 선택 목록 조회 (dayOfWeek 포함)
     */
    public List<ClassOptionRespDto> getClassOptions(String dojangCode) {
        log.info("앱 수업 선택 목록 조회: dojangCode={}", dojangCode);
        
        // ClassMst 직접 조회
        List<ClassMst> classes = classMstRepository
                .findByDojangCodeOrderByClassCodeAsc(dojangCode);
        
        // 앱 DTO로 변환 (dayOfWeek 포함)
        return classes.stream()
                .map(cls -> {
                    String classTime = formatClassTime(cls.getStartTime(), cls.getEndTime());
                    String displayText = String.format("%s %s",
                            cls.getClassName(),
                            classTime);
                    
                    return ClassOptionRespDto.builder()
                            .classCode(cls.getClassCode())
                            .displayText(displayText)
                            .dayOfWeek(cls.getDayOfWeek())
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 앱 수련 등록
     */
    @Transactional
    public void registerTraining(RegisterTrainingReqDto reqDto) {
        log.info("앱 수련 등록: studentCode={}, packageCode={}", 
                reqDto.getStudentCode(), reqDto.getPackageCode());
        
        // 앱 DTO를 웹 DTO로 변환
        com.taekwondo.miwool.dto.training.reqDto.RegisterTrainingReqDto webReqDto = 
                com.taekwondo.miwool.dto.training.reqDto.RegisterTrainingReqDto.builder()
                        .studentCode(reqDto.getStudentCode())
                        .packageCode(reqDto.getPackageCode())
                        .classCodes(reqDto.getClassCodes())
                        .useVehicle(reqDto.getUseVehicle())
                        .pickupLocation(reqDto.getPickupLocation())
                        .dropoffLocation(reqDto.getDropoffLocation())
                        .handoverMethod(reqDto.getHandoverMethod())
                        .adjustmentAmount(reqDto.getAdjustmentAmount())
                        .adjustmentDetail(reqDto.getAdjustmentDetail())
                        .trainingStartDate(reqDto.getTrainingStartDate())
                        .trainingEndDate(reqDto.getTrainingEndDate())
                        .build();
        
        // 웹 서비스 메서드 재사용
        trainingRegistrationService.registerTraining(webReqDto, reqDto.getDojangCode());
        
        log.info("앱 수련 등록 완료");
    }

    /**
     * 수업시간 포맷팅
     * "11:00~13:00"
     */
    private String formatClassTime(java.time.LocalTime startTime, java.time.LocalTime endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return startTime.format(formatter) + "~" + endTime.format(formatter);
    }
}