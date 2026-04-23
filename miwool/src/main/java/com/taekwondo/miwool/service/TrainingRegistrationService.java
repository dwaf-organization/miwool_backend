package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.training.reqDto.RegisterTrainingReqDto;
import com.taekwondo.miwool.entity.ClassMst;
import com.taekwondo.miwool.entity.MonthlyBilling;
import com.taekwondo.miwool.entity.StudentClass;
import com.taekwondo.miwool.entity.StudentTraining;
import com.taekwondo.miwool.entity.StudentTuition;
import com.taekwondo.miwool.entity.TrainingMst;
import com.taekwondo.miwool.repository.ClassMstRepository;
import com.taekwondo.miwool.repository.MonthlyBillingRepository;
import com.taekwondo.miwool.repository.StudentClassRepository;
import com.taekwondo.miwool.repository.StudentTrainingRepository;
import com.taekwondo.miwool.repository.StudentTuitionRepository;
import com.taekwondo.miwool.repository.TrainingMstRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingRegistrationService {
    
    private final TrainingMstRepository trainingMstRepository;
    private final ClassMstRepository classMstRepository;
    private final StudentTrainingRepository studentTrainingRepository;
    private final StudentClassRepository studentClassRepository;
    private final StudentTuitionRepository studentTuitionRepository;
    private final MonthlyBillingRepository monthlyBillingRepository;
    
    /**
     * 수련 등록
     * - student_training
     * - student_class
     * - student_tuition
     * - monthly_billing (첫 청구서)
     */
    @Transactional
    public void registerTraining(RegisterTrainingReqDto reqDto, String dojangCode) {
        log.info("수련 등록: studentCode={}, packageCode={}, classCodes={}", 
                reqDto.getStudentCode(), reqDto.getPackageCode(), reqDto.getClassCodes());
        
        // 1. 패키지 정보 조회 (basePrice 가져오기)
        TrainingMst trainingMst = trainingMstRepository.findById(reqDto.getPackageCode())
                .orElseThrow(() -> new IllegalArgumentException("패키지를 찾을 수 없습니다: " + reqDto.getPackageCode()));
        
        // 패키지가 본인 도장 것인지 확인
        if (!trainingMst.getDojangCode().equals(dojangCode)) {
            throw new IllegalArgumentException("해당 패키지에 대한 권한이 없습니다.");
        }
        
        // 2. 모든 수업 정보 확인
        for (String classCode : reqDto.getClassCodes()) {
            ClassMst classMst = classMstRepository.findById(classCode)
                    .orElseThrow(() -> new IllegalArgumentException("수업을 찾을 수 없습니다: " + classCode));
            
            // 수업이 본인 도장 것인지 확인
            if (!classMst.getDojangCode().equals(dojangCode)) {
                throw new IllegalArgumentException("해당 수업에 대한 권한이 없습니다: " + classCode);
            }
        }
        
        // 3. 교육비 계산
        int basePrice = trainingMst.getBasePrice();
        int adjustmentAmount = reqDto.getAdjustmentAmount() != null ? reqDto.getAdjustmentAmount() : 0;
        int actualPrice = basePrice + adjustmentAmount;
        
        LocalDate trainingStartDate = reqDto.getTrainingStartDate();
        int billingCycleDay = trainingStartDate.getDayOfMonth();
        
        // nextBillingDate = trainingStartDate + 1개월
        LocalDate nextBillingDate = trainingStartDate.plusMonths(1);
        
        // trainingEndDate = trainingStartDate + 1개월
        LocalDate trainingEndDate = trainingStartDate.plusMonths(1);
        
        // billingMonth = "YYYY-MM"
        String billingMonth = trainingStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // 4. StudentTraining 생성
        StudentTraining studentTraining = StudentTraining.builder()
                .studentCode(reqDto.getStudentCode())
                .packageCode(reqDto.getPackageCode())
                .useVehicle(reqDto.getUseVehicle())
                .pickupLocation(reqDto.getPickupLocation())
                .dropoffLocation(reqDto.getDropoffLocation())
                .handoverMethod(reqDto.getHandoverMethod())
                .startDate(trainingStartDate)
                .endDate(null)
                .isCurrent(1)
                .build();
        
        studentTrainingRepository.save(studentTraining);
        log.info("StudentTraining 생성 완료: trainingInfoCode={}", studentTraining.getTrainingInfoCode());
        
        // 5. StudentClass 생성 (여러 개) - trainingInfoCode 연결
        for (String classCode : reqDto.getClassCodes()) {
            StudentClass studentClass = StudentClass.builder()
                    .studentCode(reqDto.getStudentCode())
                    .classCode(classCode)
                    .trainingInfoCode(studentTraining.getTrainingInfoCode())  // FK 연결
                    .startDate(trainingStartDate)
                    .endDate(null)
                    .isCurrent(1)
                    .build();
            
            studentClassRepository.save(studentClass);
            log.info("StudentClass 생성 완료: classCode={}, trainingInfoCode={}", classCode, studentTraining.getTrainingInfoCode());
        }
        
        // 6. StudentTuition 생성
        StudentTuition studentTuition = StudentTuition.builder()
                .studentCode(reqDto.getStudentCode())
                .trainingInfoCode(studentTraining.getTrainingInfoCode())
                .basePrice(basePrice)
                .adjustmentAmount(adjustmentAmount)
                .adjustmentDetail(reqDto.getAdjustmentDetail())
                .actualPrice(actualPrice)
                .billingCycleDay(billingCycleDay)
                .nextBillingDate(nextBillingDate)
                .applyStartDate(trainingStartDate)
                .applyEndDate(null)
                .isCurrent(1)
                .build();
        
        studentTuitionRepository.save(studentTuition);
        log.info("StudentTuition 생성 완료: actualPrice={}, billingCycleDay={}", actualPrice, billingCycleDay);
        
        // 7. MonthlyBilling 생성 (첫 청구서)
        MonthlyBilling monthlyBilling = MonthlyBilling.builder()
                .studentCode(reqDto.getStudentCode())
                .trainingInfoCode(studentTraining.getTrainingInfoCode())
                .billingMonth(billingMonth)
                .billingDate(trainingStartDate)
                .trainingStartDate(trainingStartDate)
                .trainingEndDate(trainingEndDate)
                .billingAmount(actualPrice)
                .billingStatus("미납")
                .build();
        
        monthlyBillingRepository.save(monthlyBilling);
        log.info("MonthlyBilling 생성 완료: billingMonth={}, billingAmount={}, trainingInfoCode={}", 
                billingMonth, actualPrice, studentTraining.getTrainingInfoCode());
        
        log.info("수련 등록 완료: studentCode={}", reqDto.getStudentCode());
    }
}