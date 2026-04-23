package com.taekwondo.miwool.service.app;

import com.taekwondo.miwool.dto.app.student.respDto.StudentDetailRespDto;
import com.taekwondo.miwool.dto.app.student.respDto.StudentListRespDto;
import com.taekwondo.miwool.entity.*;
import com.taekwondo.miwool.repository.*;
import com.taekwondo.miwool.util.AgeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppStudentService {

    private final StudentRepository studentRepository;
    private final StudentClassRepository studentClassRepository;
    private final StudentGuardianRepository studentGuardianRepository;
    private final GuardianRepository guardianRepository;
    private final StudentCounselRepository studentCounselRepository;
    private final MonthlyBillingRepository monthlyBillingRepository;
    private final StudentCharacterRepository studentCharacterRepository;
    private final StudentEmotionRepository studentEmotionRepository;
    private final StudentSocialRepository studentSocialRepository;
    private final StudentClassResponseRepository studentClassResponseRepository;
    private final StudentImprovementRepository studentImprovementRepository;
    private final StudentStrengthRepository studentStrengthRepository;
    private final CommonCodeRepository commonCodeRepository;

    /**
     * 앱 제자 상세 조회
     */
    @Transactional(readOnly = true)
    public StudentDetailRespDto getStudentDetail(String dojangCode, String studentCode) {
        log.info("앱 제자 상세 조회: dojangCode={}, studentCode={}", dojangCode, studentCode);
        
        // 제자 조회
        Student student = studentRepository.findById(studentCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 제자입니다."));
        
        // 1. 기본정보
        StudentDetailRespDto.BasicInfoDto basicInfo = getBasicInfo(student);
        
        // 2. 패키지정보
        List<StudentDetailRespDto.PackageInfoDto> packageInfo = getPackageInfo(studentCode);
        
        // 3. 보호자정보
        StudentDetailRespDto.GuardianInfoDto guardianInfo = getGuardianInfo(studentCode);
        
        // 4. 상담실시여부
        StudentDetailRespDto.CounselStatusDto counselStatus = getCounselStatus(studentCode);
        
        // 5. 교육비납부여부
        StudentDetailRespDto.BillingStatusDto billingStatus = getBillingStatus(studentCode);
        
        // 6. 제자특성
        StudentDetailRespDto.TraitsDto traits = getTraits(studentCode);
        
        log.info("앱 제자 상세 조회 완료");
        
        return StudentDetailRespDto.builder()
                .basicInfo(basicInfo)
                .packageInfo(packageInfo)
                .guardianInfo(guardianInfo)
                .counselStatus(counselStatus)
                .billingStatus(billingStatus)
                .traits(traits)
                .build();
    }

    // 기본정보
    private StudentDetailRespDto.BasicInfoDto getBasicInfo(Student student) {
        int age = AgeUtil.calculateKoreanAge(student.getBirthDate());
        
        // 급수명 조회
        String beltName = null;
        if (student.getBeltCode() != null) {
            beltName = commonCodeRepository.findById(student.getBeltCode())
                    .map(code -> code.getCodeName())
                    .orElse(null);
        }
        
        return StudentDetailRespDto.BasicInfoDto.builder()
                .profileImageUrl(student.getProfileImageUrl())
                .genderCode(student.getGenderCode())
                .studentName(student.getStudentName())
                .statusCode(student.getStatusCode())
                .age(age)
                .grade(student.getGrade())
                .registDate(student.getRegistDate())
                .birthDate(student.getBirthDate())
                .studentPhone(student.getStudentPhone())
                .beltCode(student.getBeltCode())
                .beltName(beltName)
                .build();
    }

    // 패키지정보
    private List<StudentDetailRespDto.PackageInfoDto> getPackageInfo(String studentCode) {
        List<Object[]> classData = studentClassRepository.findCurrentClassesByStudent(studentCode);
        
        // 패키지명별로 그룹핑
        Map<String, List<Object[]>> packageMap = new LinkedHashMap<>();
        
        for (Object[] row : classData) {
            String packageName = (String) row[0];
            
            if (!packageMap.containsKey(packageName)) {
                packageMap.put(packageName, new ArrayList<>());
            }
            packageMap.get(packageName).add(row);
        }
        
        // 패키지별로 DTO 생성
        List<StudentDetailRespDto.PackageInfoDto> result = new ArrayList<>();
        
        for (Map.Entry<String, List<Object[]>> entry : packageMap.entrySet()) {
            String packageName = entry.getKey();
            List<Object[]> classes = entry.getValue();
            
            // 첫 번째 행에서 총교육비 추출 (같은 패키지는 동일한 교육비)
            Integer totalFee = classes.isEmpty() ? 0 : ((Number) classes.get(0)[5]).intValue();
            
            // 수업 리스트 생성
            List<StudentDetailRespDto.ClassInfoDto> classInfoList = new ArrayList<>();
            for (Object[] row : classes) {
                String className = (String) row[1];
                java.sql.Time startTimeSql = (java.sql.Time) row[2];
                java.sql.Time endTimeSql = (java.sql.Time) row[3];
                String dayOfWeek = (String) row[4];
                
                String classTime = startTimeSql.toLocalTime().toString() + " ~ " + endTimeSql.toLocalTime().toString();
                
                classInfoList.add(StudentDetailRespDto.ClassInfoDto.builder()
                        .className(className)
                        .classTime(classTime)
                        .dayOfWeek(dayOfWeek)
                        .build());
            }
            
            result.add(StudentDetailRespDto.PackageInfoDto.builder()
                    .packageName(packageName)
                    .totalFee(totalFee)
                    .classes(classInfoList)
                    .build());
        }
        
        return result;
    }

    // 보호자정보 (한 명만)
    private StudentDetailRespDto.GuardianInfoDto getGuardianInfo(String studentCode) {
        List<StudentGuardian> relations = studentGuardianRepository.findByStudentCode(studentCode);
        
        if (relations.isEmpty()) {
            return null;
        }
        
        StudentGuardian relation = relations.get(0);
        Guardian guardian = guardianRepository.findById(relation.getGuardianCode()).orElse(null);
        
        if (guardian == null) {
            return null;
        }
        
        return StudentDetailRespDto.GuardianInfoDto.builder()
                .guardianName(guardian.getGuardianName())
                .relationship(relation.getRelationship())
                .guardianPhone(guardian.getGuardianPhone())
                .build();
    }

    // 상담실시여부
    private StudentDetailRespDto.CounselStatusDto getCounselStatus(String studentCode) {
        String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        
        LocalDate counselDate = studentCounselRepository.findCounselDateByMonth(studentCode, currentMonth);
        
        if (counselDate != null) {
            return StudentDetailRespDto.CounselStatusDto.builder()
                    .status("실시")
                    .counselDate(counselDate)
                    .build();
        } else {
            return StudentDetailRespDto.CounselStatusDto.builder()
                    .status("미실시")
                    .counselDate(null)
                    .build();
        }
    }

    // 교육비납부여부
    private StudentDetailRespDto.BillingStatusDto getBillingStatus(String studentCode) {
        String currentMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        List<Object[]> billingDataList = monthlyBillingRepository.findBillingByMonth(studentCode, currentMonth);
        
        if (billingDataList == null || billingDataList.isEmpty()) {
            return null;
        }
        
        Object[] billingData = billingDataList.get(0);
        
        String billingStatus = (String) billingData[0];
        java.sql.Timestamp paidAtSql = (java.sql.Timestamp) billingData[1];
        Date billingDateSql = (Date) billingData[2];
        
        LocalDateTime paidAt = paidAtSql != null ? paidAtSql.toLocalDateTime() : null;
        LocalDate billingDate = billingDateSql.toLocalDate();
        
        return StudentDetailRespDto.BillingStatusDto.builder()
                .status(billingStatus)
                .processedDate(paidAt)
                .billingDate(billingDate)
                .build();
    }

    // 제자특성
    private StudentDetailRespDto.TraitsDto getTraits(String studentCode) {
        // 1. 성향_기본 (student_character)
        List<StudentCharacter> characterList = studentCharacterRepository.findByStudentCode(studentCode);
        List<String> personalityBasic = characterList.isEmpty() ? null : 
            characterList.stream()
                .map(StudentCharacter::getCharacterCode)
                .map(code -> commonCodeRepository.findById(code)
                    .map(c -> c.getCodeName())
                    .orElse(null))
                .filter(name -> name != null)
                .collect(Collectors.toList());
        String personalityBasicEtc = characterList.stream()
            .filter(e -> e.getCharacterCode().contains("999"))
            .findFirst()
            .map(StudentCharacter::getEtcValue)
            .orElse(null);
        
        // 2. 성향_정서 (student_emotion)
        List<StudentEmotion> emotionList = studentEmotionRepository.findByStudentCode(studentCode);
        List<String> emotion = emotionList.isEmpty() ? null :
            emotionList.stream()
                .map(StudentEmotion::getEmotionCode)
                .map(code -> commonCodeRepository.findById(code)
                    .map(c -> c.getCodeName())
                    .orElse(null))
                .filter(name -> name != null)
                .collect(Collectors.toList());
        String emotionEtc = emotionList.stream()
            .filter(e -> e.getEmotionCode().contains("999"))
            .findFirst()
            .map(StudentEmotion::getEtcValue)
            .orElse(null);
        
        // 3. 성향_사회성 (student_social)
        List<StudentSocial> socialList = studentSocialRepository.findByStudentCode(studentCode);
        List<String> sociability = socialList.isEmpty() ? null :
            socialList.stream()
                .map(StudentSocial::getSocialCode)
                .map(code -> commonCodeRepository.findById(code)
                    .map(c -> c.getCodeName())
                    .orElse(null))
                .filter(name -> name != null)
                .collect(Collectors.toList());
        String sociabilityEtc = socialList.stream()
            .filter(e -> e.getSocialCode().contains("999"))
            .findFirst()
            .map(StudentSocial::getEtcValue)
            .orElse(null);
        
        // 4. 성향_수업반응 (student_class_response)
        List<StudentClassResponse> classResponseList = studentClassResponseRepository.findByStudentCode(studentCode);
        List<String> lessonResponse = classResponseList.isEmpty() ? null :
            classResponseList.stream()
                .map(StudentClassResponse::getClassResponseCode)
                .map(code -> commonCodeRepository.findById(code)
                    .map(c -> c.getCodeName())
                    .orElse(null))
                .filter(name -> name != null)
                .collect(Collectors.toList());
        String lessonResponseEtc = classResponseList.stream()
            .filter(e -> e.getClassResponseCode().contains("999"))
            .findFirst()
            .map(StudentClassResponse::getEtcValue)
            .orElse(null);
        
        // 5. 변화필요부분 (student_improvement)
        List<StudentImprovement> improvementList = studentImprovementRepository.findByStudentCode(studentCode);
        List<String> changeNeed = improvementList.isEmpty() ? null :
            improvementList.stream()
                .map(StudentImprovement::getImprovementCode)
                .map(code -> commonCodeRepository.findById(code)
                    .map(c -> c.getCodeName())
                    .orElse(null))
                .filter(name -> name != null)
                .collect(Collectors.toList());
        String changeNeedEtc = improvementList.stream()
            .filter(e -> e.getImprovementCode().contains("999"))
            .findFirst()
            .map(StudentImprovement::getEtcValue)
            .orElse(null);
        
        // 6. 강점 (student_strength)
        List<StudentStrength> strengthList = studentStrengthRepository.findByStudentCode(studentCode);
        List<String> strength = strengthList.isEmpty() ? null :
            strengthList.stream()
                .map(StudentStrength::getStrengthCode)
                .map(code -> commonCodeRepository.findById(code)
                    .map(c -> c.getCodeName())
                    .orElse(null))
                .filter(name -> name != null)
                .collect(Collectors.toList());
        String strengthEtc = strengthList.stream()
            .filter(e -> e.getStrengthCode().contains("999"))
            .findFirst()
            .map(StudentStrength::getEtcValue)
            .orElse(null);
        
        return StudentDetailRespDto.TraitsDto.builder()
                .personalityBasic(personalityBasic)
                .personalityBasicEtc(personalityBasicEtc)
                .emotion(emotion)
                .emotionEtc(emotionEtc)
                .sociability(sociability)
                .sociabilityEtc(sociabilityEtc)
                .lessonResponse(lessonResponse)
                .lessonResponseEtc(lessonResponseEtc)
                .changeNeed(changeNeed)
                .changeNeedEtc(changeNeedEtc)
                .strength(strength)
                .strengthEtc(strengthEtc)
                .build();
    }

    /**
     * 앱 제자 목록 조회
     */
    @Transactional(readOnly = true)
    public StudentListRespDto getStudentList(
            String dojangCode,
            String studentSearch,
            String grade,
            String genderCode,
            String beltCode,
            String statusCode,
            int page,
            int size) {
        
        log.info("앱 제자 목록 조회: dojangCode={}, studentSearch={}, grade={}, genderCode={}, beltCode={}, statusCode={}, page={}, size={}",
                dojangCode, studentSearch, grade, genderCode, beltCode, statusCode, page, size);
        
        // 페이지는 1부터 시작하므로 -1 처리
        Pageable pageable = PageRequest.of(page - 1, size);
        
        Page<Object[]> studentPage = studentRepository.findStudentsForApp(
                dojangCode,
                studentSearch,
                grade,
                genderCode,
                beltCode,
                statusCode,
                pageable);
        
        List<StudentListRespDto.StudentDto> students = new ArrayList<>();
        
        for (Object[] row : studentPage.getContent()) {
            String studentCode = (String) row[0];
            String studentName = (String) row[1];
            Integer genderCodeInt = (Integer) row[2];
            Date birthDateSql = (Date) row[3];
            LocalDate birthDate = birthDateSql.toLocalDate();
            String gradeStr = (String) row[4];
            String beltName = (String) row[5];
            String studentPhone = (String) row[6];
            String statusCodeStr = (String) row[7];
            Date registDateSql = (Date) row[8];
            LocalDate registDate = registDateSql.toLocalDate();
            LocalDateTime deletedAt = row[9] != null ? ((java.sql.Timestamp) row[9]).toLocalDateTime() : null;
            
            int age = AgeUtil.calculateKoreanAge(birthDate);
            
            students.add(StudentListRespDto.StudentDto.builder()
                    .studentCode(studentCode)
                    .studentName(studentName)
                    .genderCode(genderCodeInt)
                    .age(age)
                    .grade(gradeStr)
                    .beltName(beltName)
                    .studentPhone(studentPhone)
                    .statusCode(statusCodeStr)
                    .registDate(registDate)
                    .deletedAt(deletedAt)
                    .build());
        }
        
        log.info("앱 제자 목록 조회 완료: {}건 / 전체 {}건", students.size(), studentPage.getTotalElements());
        
        return StudentListRespDto.builder()
                .students(students)
                .totalElements(studentPage.getTotalElements())
                .totalPages(studentPage.getTotalPages())
                .currentPage(page) // 원래 요청받은 페이지 번호 반환
                .size(size)
                .build();
    }
}