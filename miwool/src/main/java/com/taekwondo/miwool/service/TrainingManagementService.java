package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.training.reqDto.UpdateTrainingReqDto;
import com.taekwondo.miwool.entity.*;
import com.taekwondo.miwool.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingManagementService {
    
    private final StudentTrainingRepository studentTrainingRepository;
    private final StudentClassRepository studentClassRepository;
    private final StudentTuitionRepository studentTuitionRepository;
    private final MonthlyBillingRepository monthlyBillingRepository;
    private final TuitionPaymentRepository tuitionPaymentRepository;
    private final TrainingMstRepository trainingMstRepository;
    private final ClassMstRepository classMstRepository;
    
    /**
     * 수련 수정
     */
    @Transactional
    public void updateTraining(UpdateTrainingReqDto reqDto, String dojangCode) {
        log.info("수련 수정: trainingInfoCode={}", reqDto.getTrainingInfoCode());
        
        // 1. 기존 수련정보 조회
        StudentTraining oldTraining = studentTrainingRepository.findById(reqDto.getTrainingInfoCode())
                .orElseThrow(() -> new IllegalArgumentException("수련정보를 찾을 수 없습니다: " + reqDto.getTrainingInfoCode()));
        
        StudentTuition oldTuition = studentTuitionRepository.findByTrainingInfoCode(reqDto.getTrainingInfoCode())
                .orElseThrow(() -> new IllegalArgumentException("교육비정보를 찾을 수 없습니다"));
        
        // 변경 사항 감지
        boolean packageChanged = !oldTraining.getPackageCode().equals(reqDto.getPackageCode());
        boolean startDateChanged = !oldTraining.getStartDate().equals(reqDto.getTrainingStartDate());
        boolean adjustmentChanged = !oldTuition.getAdjustmentAmount().equals(
                reqDto.getAdjustmentAmount() != null ? reqDto.getAdjustmentAmount() : 0);
        
        log.info("변경 감지: package={}, startDate={}, adjustment={}", 
                packageChanged, startDateChanged, adjustmentChanged);
        
        // 2. 패키지 변경 시
        if (packageChanged) {
            handlePackageChange(reqDto, oldTraining, dojangCode);
            return;  // 패키지 변경 시 모든 처리 완료
        }
        
        // 3. 수련시작일 변경 시
        if (startDateChanged) {
            handleStartDateChange(reqDto, oldTraining);
        }
        
        // 4. 조정금액 변경 시
        if (adjustmentChanged) {
            handleAdjustmentChange(reqDto, oldTraining, oldTuition);
        }
        
        // 5. 수업 변경 (항상 체크)
        handleClassChange(reqDto, oldTraining);
        
        // 6. 차량정보 변경 (항상 업데이트)
        oldTraining.setUseVehicle(reqDto.getUseVehicle());
        oldTraining.setPickupLocation(reqDto.getPickupLocation());
        oldTraining.setDropoffLocation(reqDto.getDropoffLocation());
        oldTraining.setHandoverMethod(reqDto.getHandoverMethod());
        studentTrainingRepository.save(oldTraining);
        
        log.info("수련 수정 완료: trainingInfoCode={}", reqDto.getTrainingInfoCode());
    }
    
    /**
     * 패키지 변경 처리
     */
    private void handlePackageChange(UpdateTrainingReqDto reqDto, StudentTraining oldTraining, String dojangCode) {
        log.info("패키지 변경 처리: {} -> {}", oldTraining.getPackageCode(), reqDto.getPackageCode());
        
        // 새 패키지 정보 조회
        TrainingMst newPackage = trainingMstRepository.findById(reqDto.getPackageCode())
                .orElseThrow(() -> new IllegalArgumentException("패키지를 찾을 수 없습니다: " + reqDto.getPackageCode()));
        
        if (!newPackage.getDojangCode().equals(dojangCode)) {
            throw new IllegalArgumentException("해당 패키지에 대한 권한이 없습니다.");
        }
        
        int newBasePrice = newPackage.getBasePrice();
        int newAdjustmentAmount = reqDto.getAdjustmentAmount() != null ? reqDto.getAdjustmentAmount() : 0;
        int newActualPrice = newBasePrice + newAdjustmentAmount;
        
        // 현재 달
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // 1. student_training 수정
        oldTraining.setPackageCode(reqDto.getPackageCode());
        oldTraining.setUseVehicle(reqDto.getUseVehicle());
        oldTraining.setPickupLocation(reqDto.getPickupLocation());
        oldTraining.setDropoffLocation(reqDto.getDropoffLocation());
        oldTraining.setHandoverMethod(reqDto.getHandoverMethod());
        studentTrainingRepository.save(oldTraining);
        
        // 2. student_class 삭제 후 재생성
        studentClassRepository.deleteByTrainingInfoCode(reqDto.getTrainingInfoCode());
        for (String classCode : reqDto.getClassCodes()) {
            // 수업 권한 확인
            ClassMst classMst = classMstRepository.findById(classCode)
                    .orElseThrow(() -> new IllegalArgumentException("수업을 찾을 수 없습니다: " + classCode));
            if (!classMst.getDojangCode().equals(dojangCode)) {
                throw new IllegalArgumentException("해당 수업에 대한 권한이 없습니다: " + classCode);
            }
            
            StudentClass studentClass = StudentClass.builder()
                    .studentCode(reqDto.getStudentCode())
                    .classCode(classCode)
                    .trainingInfoCode(reqDto.getTrainingInfoCode())
                    .startDate(reqDto.getTrainingStartDate())
                    .endDate(null)
                    .isCurrent(1)
                    .build();
            studentClassRepository.save(studentClass);
        }
        
        // 3. student_tuition 수정
        StudentTuition tuition = studentTuitionRepository.findByTrainingInfoCode(reqDto.getTrainingInfoCode())
                .orElseThrow(() -> new IllegalArgumentException("교육비정보를 찾을 수 없습니다"));
        
        tuition.setTrainingInfoCode(reqDto.getTrainingInfoCode());
        tuition.setBasePrice(newBasePrice);
        tuition.setAdjustmentAmount(newAdjustmentAmount);
        tuition.setAdjustmentDetail(reqDto.getAdjustmentDetail());
        tuition.setActualPrice(newActualPrice);
        studentTuitionRepository.save(tuition);
        
        // 4. monthly_billing 삭제 후 재생성 (해당 달만)
        monthlyBillingRepository.deleteByTrainingInfoCodeAndBillingMonth(
                reqDto.getTrainingInfoCode(), currentMonth);
        
        LocalDate trainingStartDate = reqDto.getTrainingStartDate();
        LocalDate trainingEndDate = trainingStartDate.plusMonths(1);
        
        MonthlyBilling newBilling = MonthlyBilling.builder()
                .studentCode(reqDto.getStudentCode())
                .trainingInfoCode(reqDto.getTrainingInfoCode())
                .billingMonth(currentMonth)
                .billingDate(trainingStartDate)
                .trainingStartDate(trainingStartDate)
                .trainingEndDate(trainingEndDate)
                .billingAmount(newActualPrice)
                .billingStatus("미납")
                .build();
        monthlyBillingRepository.save(newBilling);
        
        // 5. tuition_payment 삭제 (완납 건만)
        List<MonthlyBilling> completedBillings = monthlyBillingRepository
                .findByTrainingInfoCodeAndBillingStatus(reqDto.getTrainingInfoCode(), "완납");
        
        for (MonthlyBilling billing : completedBillings) {
            tuitionPaymentRepository.deleteByBillingCode(billing.getBillingCode());
        }
        
        log.info("패키지 변경 완료");
    }
    
    /**
     * 수련시작일 변경 처리
     */
    private void handleStartDateChange(UpdateTrainingReqDto reqDto, StudentTraining oldTraining) {
        log.info("수련시작일 변경 처리: {} -> {}", oldTraining.getStartDate(), reqDto.getTrainingStartDate());
        
        LocalDate newStartDate = reqDto.getTrainingStartDate();
        int newBillingCycleDay = newStartDate.getDayOfMonth();
        LocalDate newNextBillingDate = newStartDate.plusMonths(1);
        LocalDate newTrainingEndDate = newStartDate.plusMonths(1);
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // 1. student_training 수정
        oldTraining.setStartDate(newStartDate);
        studentTrainingRepository.save(oldTraining);
        
        // 2. student_class 수정
        studentClassRepository.updateStartDateByTrainingInfoCode(
                reqDto.getTrainingInfoCode(), newStartDate);
        
        // 3. student_tuition 수정
        StudentTuition tuition = studentTuitionRepository.findByTrainingInfoCode(reqDto.getTrainingInfoCode())
                .orElseThrow(() -> new IllegalArgumentException("교육비정보를 찾을 수 없습니다"));
        
        tuition.setBillingCycleDay(newBillingCycleDay);
        tuition.setNextBillingDate(newNextBillingDate);
        tuition.setApplyStartDate(newStartDate);
        studentTuitionRepository.save(tuition);
        
        // 4. monthly_billing 날짜만 수정 (삭제 안 함!)
        monthlyBillingRepository.updateDatesByTrainingInfoCodeAndBillingMonth(
                reqDto.getTrainingInfoCode(), currentMonth, 
                newStartDate, newStartDate, newTrainingEndDate);
        
        log.info("수련시작일 변경 완료");
    }
    
    /**
     * 조정금액 변경 처리
     */
    private void handleAdjustmentChange(UpdateTrainingReqDto reqDto, StudentTraining oldTraining, StudentTuition oldTuition) {
        log.info("조정금액 변경 처리");
        
        int newAdjustmentAmount = reqDto.getAdjustmentAmount() != null ? reqDto.getAdjustmentAmount() : 0;
        int newActualPrice = oldTuition.getBasePrice() + newAdjustmentAmount;
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // 1. student_tuition 수정
        oldTuition.setAdjustmentAmount(newAdjustmentAmount);
        oldTuition.setAdjustmentDetail(reqDto.getAdjustmentDetail());
        oldTuition.setActualPrice(newActualPrice);
        studentTuitionRepository.save(oldTuition);
        
        // 2. monthly_billing 청구금액 수정
        monthlyBillingRepository.updateBillingAmountByTrainingInfoCodeAndBillingMonth(
                reqDto.getTrainingInfoCode(), currentMonth, newActualPrice);
        
        // 3. tuition_payment 납부금액 수정
        List<MonthlyBilling> currentMonthBillings = monthlyBillingRepository
                .findByTrainingInfoCodeAndBillingMonth(reqDto.getTrainingInfoCode(), currentMonth);
        
        for (MonthlyBilling billing : currentMonthBillings) {
            tuitionPaymentRepository.updatePaymentAmountByBillingCode(
                    billing.getBillingCode(), newActualPrice);
        }
        
        log.info("조정금액 변경 완료");
    }
    
    /**
     * 수업 변경 처리
     */
    private void handleClassChange(UpdateTrainingReqDto reqDto, StudentTraining oldTraining) {
        log.info("수업 변경 처리");
        
        // student_class 삭제 후 재생성
        studentClassRepository.deleteByTrainingInfoCode(reqDto.getTrainingInfoCode());
        
        for (String classCode : reqDto.getClassCodes()) {
            StudentClass studentClass = StudentClass.builder()
                    .studentCode(reqDto.getStudentCode())
                    .classCode(classCode)
                    .trainingInfoCode(reqDto.getTrainingInfoCode())
                    .startDate(oldTraining.getStartDate())  // 기존 시작일 유지
                    .endDate(null)
                    .isCurrent(1)
                    .build();
            studentClassRepository.save(studentClass);
        }
        
        log.info("수업 변경 완료");
    }
    
    /**
     * 수련 삭제
     */
    @Transactional
    public void deleteTraining(Integer trainingInfoCode, String dojangCode) {
        log.info("수련 삭제: trainingInfoCode={}", trainingInfoCode);
        
        // 수련정보 조회
        StudentTraining training = studentTrainingRepository.findById(trainingInfoCode)
                .orElseThrow(() -> new IllegalArgumentException("수련정보를 찾을 수 없습니다: " + trainingInfoCode));
        
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // 1. student_class 삭제
        studentClassRepository.deleteByTrainingInfoCode(trainingInfoCode);
        log.info("student_class 삭제 완료");
        
        // 2. monthly_billing 삭제 (해당 달 + 미납만)
        monthlyBillingRepository.deleteByTrainingInfoCodeAndBillingMonthAndBillingStatusNot(
                trainingInfoCode, currentMonth, "완납");
        log.info("monthly_billing 삭제 완료 (해당 달 + 미납)");
        
        // 3. tuition_payment는 건드리지 않음
        
        // 4. student_tuition 삭제
        studentTuitionRepository.deleteByTrainingInfoCode(trainingInfoCode);
        log.info("student_tuition 삭제 완료");
        
        // 5. student_training 삭제 (마지막)
        studentTrainingRepository.delete(training);
        log.info("student_training 삭제 완료");
        
        log.info("수련 삭제 완료: trainingInfoCode={}", trainingInfoCode);
    }
}