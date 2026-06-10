package com.taekwondo.miwool.scheduler;

import com.taekwondo.miwool.entity.Alarm;
import com.taekwondo.miwool.entity.Guardian;
import com.taekwondo.miwool.entity.Student;
import com.taekwondo.miwool.entity.StudentGuardian;
import com.taekwondo.miwool.repository.AlarmRepository;
import com.taekwondo.miwool.repository.GuardianRepository;
import com.taekwondo.miwool.repository.StudentGuardianRepository;
import com.taekwondo.miwool.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmScheduler {

    private final StudentRepository studentRepository;
    private final GuardianRepository guardianRepository;
    private final StudentGuardianRepository studentGuardianRepository;
    private final AlarmRepository alarmRepository;

    /**
     * 매일 자정에 생일 및 기념일 알림 생성
     */
    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(cron = "0 15 9 * * *")
    public void createDailyAlarms() {
        log.info("일일 알림 생성 시작");
        
        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();
        
        // 1. 제자 생일 알림
        createStudentBirthdayAlarms(month, day, today);
        
        // 2. 보호자 생일 알림
//        createGuardianBirthdayAlarms(month, day, today);
        
        // 3. 결혼기념일 알림
//        createAnniversaryAlarms(month, day, today);
        
        log.info("일일 알림 생성 완료");
    }

    // 제자 생일 알림
    private void createStudentBirthdayAlarms(int month, int day, LocalDate today) {
        List<Student> birthdayStudents = studentRepository.findByBirthMonthAndDay(month, day);
        log.info("오늘 생일인 제자: {}명", birthdayStudents.size());
        
        for (Student student : birthdayStudents) {
            // 중복 체크
            if (alarmRepository.existsByDojangCodeAndAlarmTypeAndTargetCodeAndAlarmDate(
                    student.getDojangCode(), "제자 생일", student.getStudentCode(), today)) {
                continue;
            }
            
            Alarm alarm = Alarm.builder()
                    .dojangCode(student.getDojangCode())
                    .alarmType("제자 생일")
                    .targetCode(student.getStudentCode())
                    .targetName(student.getStudentName())
                    .studentCode(student.getStudentCode())
                    .alarmDate(today)
                    .alarmMessage(student.getStudentName() + " 학생의 생일입니다.")
                    .build();
            
            alarmRepository.save(alarm);
            log.info("제자 생일 알림 생성: {}", student.getStudentName());
        }
    }

    // 보호자 생일 알림
    private void createGuardianBirthdayAlarms(int month, int day, LocalDate today) {
        List<Guardian> birthdayGuardians = guardianRepository.findByBirthMonthAndDay(month, day);
        log.info("오늘 생일인 보호자: {}명", birthdayGuardians.size());
        
        for (Guardian guardian : birthdayGuardians) {
            // 해당 보호자와 연결된 제자들 조회
            List<StudentGuardian> relations = studentGuardianRepository.findByGuardianCode(guardian.getGuardianCode());
            
            for (StudentGuardian relation : relations) {
                // 중복 체크
                if (alarmRepository.existsByDojangCodeAndAlarmTypeAndTargetCodeAndAlarmDate(
                        guardian.getDojangCode(), "보호자 생일", guardian.getGuardianCode(), today)) {
                    continue;
                }
                
                String targetName = guardian.getGuardianName() + "(" + relation.getRelationship() + ")";
                
                Alarm alarm = Alarm.builder()
                        .dojangCode(guardian.getDojangCode())
                        .alarmType("보호자 생일")
                        .targetCode(guardian.getGuardianCode())
                        .targetName(targetName)
                        .relation(relation.getRelationship())
                        .studentCode(relation.getStudentCode())
                        .alarmDate(today)
                        .alarmMessage(targetName + "님의 생일입니다.")
                        .build();
                
                alarmRepository.save(alarm);
                log.info("보호자 생일 알림 생성: {}", targetName);
                break; // 한 보호자당 한 번만 알림 생성
            }
        }
    }

    // 결혼기념일 알림
    private void createAnniversaryAlarms(int month, int day, LocalDate today) {
        List<Guardian> anniversaryGuardians = guardianRepository.findByAnniversaryMonthAndDay(month, day);
        log.info("오늘 결혼기념일인 보호자: {}명", anniversaryGuardians.size());
        
        for (Guardian guardian : anniversaryGuardians) {
            // 해당 보호자와 연결된 제자들 조회
            List<StudentGuardian> relations = studentGuardianRepository.findByGuardianCode(guardian.getGuardianCode());
            
            for (StudentGuardian relation : relations) {
                // 중복 체크
                if (alarmRepository.existsByDojangCodeAndAlarmTypeAndTargetCodeAndAlarmDate(
                        guardian.getDojangCode(), "보호자 결혼기념일", guardian.getGuardianCode(), today)) {
                    continue;
                }
                
                String targetName = guardian.getGuardianName() + "(" + relation.getRelationship() + ")";
                
                Alarm alarm = Alarm.builder()
                        .dojangCode(guardian.getDojangCode())
                        .alarmType("보호자 결혼기념일")
                        .targetCode(guardian.getGuardianCode())
                        .targetName(targetName)
                        .relation(relation.getRelationship())
                        .studentCode(relation.getStudentCode())
                        .alarmDate(today)
                        .alarmMessage(targetName + "님의 결혼기념일입니다.")
                        .build();
                
                alarmRepository.save(alarm);
                log.info("결혼기념일 알림 생성: {}", targetName);
                break; // 한 보호자당 한 번만 알림 생성
            }
        }
    }
}