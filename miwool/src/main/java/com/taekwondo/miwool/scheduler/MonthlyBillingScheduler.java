package com.taekwondo.miwool.scheduler;

import com.taekwondo.miwool.entity.MonthlyBilling;
import com.taekwondo.miwool.entity.StudentTraining;
import com.taekwondo.miwool.entity.StudentTuition;
import com.taekwondo.miwool.repository.MonthlyBillingRepository;
import com.taekwondo.miwool.repository.StudentTrainingRepository;
import com.taekwondo.miwool.repository.StudentTuitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonthlyBillingScheduler {

    private final StudentTrainingRepository studentTrainingRepository;
    private final StudentTuitionRepository studentTuitionRepository;
    private final MonthlyBillingRepository monthlyBillingRepository;

    /**
     * 매월 1일 01:00에 청구서 자동 발행
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 1 1 * ?")
    @Transactional
    public void generateMonthlyBillings() {
        log.info("=== 월별 청구서 자동 발행 시작 ===");
        
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate today = LocalDate.now();
        
        // 1. 활성화된 수련정보 조회 (퇴관 학생 제외)
        List<StudentTraining> activeTrainings = studentTrainingRepository
            .findActiveTrainingsExcludingDeletedStudents();
        
        log.info("활성 수련정보 조회 완료: {}건", activeTrainings.size());
        
        int successCount = 0;
        int skipCount = 0;
        int errorCount = 0;
        
        for (StudentTraining training : activeTrainings) {
            try {
                // 2. 중복 청구 체크
                boolean exists = monthlyBillingRepository
                    .existsByTrainingInfoCodeAndBillingMonth(
                        training.getTrainingInfoCode(), currentMonth);
                
                if (exists) {
                    log.debug("이미 청구서 존재: trainingInfoCode={}, month={}", 
                        training.getTrainingInfoCode(), currentMonth);
                    skipCount++;
                    continue;
                }
                
                // 3. 교육비 정보 조회
                StudentTuition tuition = studentTuitionRepository
                    .findByTrainingInfoCodeAndIsCurrent(
                        training.getTrainingInfoCode(), 1)
                    .orElse(null);
                
                if (tuition == null) {
                    log.warn("교육비 정보 없음: trainingInfoCode={}", 
                        training.getTrainingInfoCode());
                    skipCount++;
                    continue;
                }
                
                // 4. 청구서 생성
                MonthlyBilling billing = MonthlyBilling.builder()
                    .studentCode(training.getStudentCode())
                    .trainingInfoCode(training.getTrainingInfoCode())
                    .billingMonth(currentMonth)
                    .billingDate(today)
                    .trainingStartDate(training.getStartDate())
                    .trainingEndDate(training.getStartDate().plusMonths(1))
                    .billingAmount(tuition.getActualPrice())
                    .billingStatus("미납")
                    .build();
                
                monthlyBillingRepository.save(billing);
                
                log.debug("청구서 발행 성공: studentCode={}, trainingInfoCode={}, amount={}", 
                    training.getStudentCode(), training.getTrainingInfoCode(), 
                    tuition.getActualPrice());
                
                successCount++;
                
            } catch (Exception e) {
                log.error("청구서 발행 실패: trainingInfoCode={}, error={}", 
                    training.getTrainingInfoCode(), e.getMessage(), e);
                errorCount++;
            }
        }
        
        log.info("=== 월별 청구서 자동 발행 완료 ===");
        log.info("성공: {}건, 스킵: {}건, 실패: {}건", successCount, skipCount, errorCount);
    }
}