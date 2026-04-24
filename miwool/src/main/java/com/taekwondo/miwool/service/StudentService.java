package com.taekwondo.miwool.service;

import com.taekwondo.miwool.common.dto.PageInfo;
import com.taekwondo.miwool.dto.student.reqDto.CreateBeltHistoryReqDto;
import com.taekwondo.miwool.dto.student.reqDto.RegisterStudentReqDto;
import com.taekwondo.miwool.dto.student.reqDto.SaveCharacterTraitReqDto;
import com.taekwondo.miwool.dto.student.reqDto.StudentListReqDto;
import com.taekwondo.miwool.dto.student.reqDto.UpdateStudentBasicInfoReqDto;
import com.taekwondo.miwool.dto.student.respDto.BeltHistoryItemDto;
import com.taekwondo.miwool.dto.student.respDto.BeltHistoryListRespDto;
import com.taekwondo.miwool.dto.student.respDto.CharacterTraitInfoRespDto;
import com.taekwondo.miwool.dto.student.respDto.RegisterStudentRespDto;
import com.taekwondo.miwool.dto.student.respDto.StudentBasicInfoRespDto;
import com.taekwondo.miwool.dto.student.respDto.StudentListRespDto;
import com.taekwondo.miwool.entity.*;
import com.taekwondo.miwool.repository.*;
import com.taekwondo.miwool.util.AgeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {
    
    private final StudentRepository studentRepository;
    private final GuardianRepository guardianRepository;
    private final StudentGuardianRepository studentGuardianRepository;
    private final StudentBeltRepository studentBeltRepository;
    private final StudentStatusRepository studentStatusRepository;
    private final StudentPurposeRepository studentPurposeRepository;
    private final CommonCodeRepository commonCodeRepository;
    private final MonthlyBillingRepository monthlyBillingRepository;
    private final StudentTrainingRepository studentTrainingRepository;
    private final StudentClassRepository studentClassRepository;
    private final StudentTuitionRepository studentTuitionRepository;
    
    private final StudentCharacterRepository studentCharacterRepository;
    private final StudentEmotionRepository studentEmotionRepository;
    private final StudentSocialRepository studentSocialRepository;
    private final StudentClassResponseRepository studentClassResponseRepository;
    private final StudentHealthRepository studentHealthRepository;
    private final StudentBodyTypeRepository studentBodyTypeRepository;
    private final StudentBodyPartRepository studentBodyPartRepository;
    private final StudentImprovementRepository studentImprovementRepository;
    private final StudentStrengthRepository studentStrengthRepository;
    
    /**
     * 제자 등록 (Step 1)
     */
    @Transactional
    public RegisterStudentRespDto registerStudent(String dojangCode, RegisterStudentReqDto reqDto) {
        
        // 1. 제자 코드 생성 (도장명-S+YYYYMM+seq)
        String studentCode = generateStudentCode(dojangCode);
        
        // 2. 보호자 코드 생성 (도장명-G+YYYYMM+seq)
        String guardianCode = generateGuardianCode(dojangCode);
        
        // 3. Student 엔티티 생성 및 저장
        Student student = Student.builder()
                .studentCode(studentCode)
                .dojangCode(dojangCode)
                .studentName(reqDto.getStudentName())
                .studentNameEn(reqDto.getStudentNameEn())
                .registDate(reqDto.getRegistDate())
                .birthDate(reqDto.getBirthDate())
                .genderCode(reqDto.getGenderCode())
                .genderName(reqDto.getGenderName())
                .studentPhone(reqDto.getStudentPhone())
                .studentZipcode(reqDto.getStudentZipcode())
                .studentAdd(reqDto.getStudentAdd())
                .studentAdd2(reqDto.getStudentAdd2())
                .schoolName(reqDto.getSchoolName())
                .grade(reqDto.getGrade())
                .className(reqDto.getClassName())
                .statusCode("재원")
                .beltCode(reqDto.getBeltCode())
                .registPathCode(reqDto.getRegistPathCode())
                .registReason(reqDto.getRegistReason())
                .hasExerciseHistory(reqDto.getHasExerciseHistory())
                .previousSports(reqDto.getPreviousSports())
                .previousDojangExp(reqDto.getPreviousDojangExp())
                .hasMedication(reqDto.getHasMedication())
                .hasAllergy(reqDto.getHasAllergy())
                .healthNote(reqDto.getHealthNote())
                .isDeleted(0)
                .build();
        
        studentRepository.save(student);
        
        // 4. Guardian 엔티티 생성 및 저장
        Guardian guardian = Guardian.builder()
                .guardianCode(guardianCode)
                .dojangCode(dojangCode)
                .guardianName(reqDto.getGuardianName())
                .guardianPhone(reqDto.getGuardianPhone())
                .guardianEmergencyPhone(reqDto.getGuardianEmergencyPhone())
                .guardianBirthDate(reqDto.getGuardianBirthDate())
                .guardianJob(reqDto.getGuardianJob())
                .guardianAnniversaryDate(reqDto.getGuardianAnniversaryDate())
                .guardianRequest(reqDto.getGuardianRequest())
                .build();
        
        guardianRepository.save(guardian);
        
        // 5. StudentGuardian 관계 저장
        StudentGuardian studentGuardian = StudentGuardian.builder()
                .studentCode(studentCode)
                .guardianCode(guardianCode)
                .relationship(reqDto.getGuardianRelationship())
                .build();
        
        studentGuardianRepository.save(studentGuardian);
        
        // 6. StudentBelt 급수 이력 저장
        String beltHistoryCode = generateBeltHistoryCode(dojangCode);
        StudentBelt studentBelt = StudentBelt.builder()
                .beltHistoryCode(beltHistoryCode)
                .studentCode(studentCode)
                .beltCode(reqDto.getBeltCode())
                .taekwondoMonths(reqDto.getTaekwondoMonths() != null ? reqDto.getTaekwondoMonths() : 0)
                .acquiredAt(reqDto.getRegistDate())
                .build();
        
        studentBeltRepository.save(studentBelt);
        
        // 7. StudentPurpose 등록목적 다중 저장
        if (reqDto.getPurposeCodes() != null && !reqDto.getPurposeCodes().isEmpty()) {
            for (String purposeCode : reqDto.getPurposeCodes()) {
                // PURPOSE_009(기타)일 때만 etcValue 저장
                String etcValue = "PURPOSE_009".equals(purposeCode) ? reqDto.getPurposeEtcValue() : null;
                
                StudentPurpose studentPurpose = StudentPurpose.builder()
                        .studentCode(studentCode)
                        .purposeCode(purposeCode)
                        .etcValue(etcValue)
                        .build();
                studentPurposeRepository.save(studentPurpose);
            }
        }
        
        // 8. StudentStatus 재원상태 이력 저장
        String statusHistoryCode = generateStatusHistoryCode(dojangCode);
        StudentStatus studentStatus = StudentStatus.builder()
                .statusHistoryCode(statusHistoryCode)
                .studentCode(studentCode)
                .statusCode("재원")  // 초기 상태
                .changeDate(reqDto.getRegistDate())  // 입관일
                .statusReason("신규 입관")
                .build();
        
        studentStatusRepository.save(studentStatus);
        
        log.info("제자 등록 완료: {} ({}), 보호자: {} ({})", 
                student.getStudentName(), studentCode,
                guardian.getGuardianName(), guardianCode);
        
        // 9. 응답 DTO 생성
        return RegisterStudentRespDto.builder()
                .studentCode(studentCode)
                .studentName(student.getStudentName())
                .registDate(student.getRegistDate())
                .beltCode(reqDto.getBeltCode())
                .statusCode("재원")
                .guardianCode(guardianCode)
                .guardianName(guardian.getGuardianName())
                .guardianRelationship(reqDto.getGuardianRelationship())
                .build();
    }
    
    /**
     * 제자 코드 생성 (도장코드-SYYnnn)
     * 예: MW26001-S26001
     */
    private String generateStudentCode(String dojangCode) {
        String year = String.valueOf(LocalDate.now().getYear()).substring(2); // "26"
        String prefix = dojangCode + "-S" + year; // "MW26001-S26"

        // prefix로 시작하는 가장 마지막 코드를 가져옴
        return studentRepository.findFirstByStudentCodeStartingWithOrderByStudentCodeDesc(prefix)
                .map(student -> {
                    String lastCode = student.getStudentCode();
                    int nextSeq = Integer.parseInt(lastCode.substring(lastCode.length() - 3)) + 1;
                    return prefix + String.format("%03d", nextSeq);
                })
                .orElse(prefix + "001"); // 해당 연도 첫 등록이면 001 부여
    }

    /**
     * 보호자 코드 생성 (도장코드-GYYnnn)
     * 예: MW26001-G26001
     */
    private String generateGuardianCode(String dojangCode) {
        String year = String.valueOf(LocalDate.now().getYear()).substring(2);
        String prefix = dojangCode + "-G" + year;

        return guardianRepository.findFirstByGuardianCodeStartingWithOrderByGuardianCodeDesc(prefix)
                .map(guardian -> {
                    String lastCode = guardian.getGuardianCode();
                    int nextSeq = Integer.parseInt(lastCode.substring(lastCode.length() - 3)) + 1;
                    return prefix + String.format("%03d", nextSeq);
                })
                .orElse(prefix + "001");
    }

    /**
     * 재원상태 이력 코드 생성 (도장코드-SSYYnnn)
     * 예: MW26001-SS26001
     */
    private String generateStatusHistoryCode(String dojangCode) {
        String year = String.valueOf(LocalDate.now().getYear()).substring(2);
        String prefix = dojangCode + "-SS" + year;

        return studentStatusRepository.findFirstByStatusHistoryCodeStartingWithOrderByStatusHistoryCodeDesc(prefix)
                .map(history -> {
                    String lastCode = history.getStatusHistoryCode();
                    int nextSeq = Integer.parseInt(lastCode.substring(lastCode.length() - 3)) + 1;
                    return prefix + String.format("%03d", nextSeq);
                })
                .orElse(prefix + "001");
    }

    /**
     * 급수 이력 코드 생성 (도장코드-SBYYnnn)
     * 예: MW26001-SB26001
     */
    private String generateBeltHistoryCode(String dojangCode) {
        String year = String.valueOf(LocalDate.now().getYear()).substring(2);
        String prefix = dojangCode + "-SB" + year;

        return studentBeltRepository.findFirstByBeltHistoryCodeStartingWithOrderByBeltHistoryCodeDesc(prefix)
                .map(history -> {
                    String lastCode = history.getBeltHistoryCode();
                    int nextSeq = Integer.parseInt(lastCode.substring(lastCode.length() - 3)) + 1;
                    return prefix + String.format("%03d", nextSeq);
                })
                .orElse(prefix + "001");
    }
    
    /**
     * 제자 목록 조회 (Native Query 사용 - 완전한 DB 처리)
     * 모든 필터링 + 페이징을 DB에서 처리하여 최고 성능 달성
     */
    public List<StudentListRespDto> getStudentList(String dojangCode, StudentListReqDto reqDto) {
        
        log.info("제자 목록 조회 시작: dojangCode={}, reqDto={}", dojangCode, reqDto);
        
        // 1. DB에서 완전히 필터링된 데이터 조회 (1번의 쿼리)
        List<Object[]> rawResults = studentRepository.findStudentListNative(
                dojangCode,
                reqDto.getStudentSearch(),
                reqDto.getGenderCode(),
                reqDto.getBeltCode(),
                reqDto.getStatusCode(),
                reqDto.getGradeCode()
        );
        
        // 2. 전체 개수 조회 (페이징용)
        long totalElements = studentRepository.countStudentListNative(
                dojangCode,
                reqDto.getStudentSearch(),
                reqDto.getGenderCode(),
                reqDto.getBeltCode(),
                reqDto.getStatusCode(),
                reqDto.getGradeCode()
        );
        
        log.info("DB 조회 완료: {}건 / 전체 {}건", rawResults.size(), totalElements);
        
        // 3. Object[]를 DTO로 변환 (필터링 없이 바로 변환)
        List<StudentListRespDto> resultList = new ArrayList<>();
        
        for (Object[] row : rawResults) {
            String studentCode = (String) row[0];
            String studentName = (String) row[1];
            java.sql.Date birthDateSql = (java.sql.Date) row[2];
            LocalDate birthDate = birthDateSql.toLocalDate();
            Integer genderCode = (Integer) row[3];
            String genderName = (String) row[4];
            String gradeFromDb = (String) row[5];
            String statusCode = (String) row[6];
            String beltCode = (String) row[7];
            java.sql.Date registDateSql = (java.sql.Date) row[8];
            LocalDate registDate = registDateSql.toLocalDate();
            java.sql.Timestamp deletedAtTs = (java.sql.Timestamp) row[9];
            
            // 한국 나이 계산
            int age = AgeUtil.calculateKoreanAge(birthDate);
            
            // 학년은 DB에서 가져온 값 사용 (계산하지 않음)
            String grade = gradeFromDb != null ? gradeFromDb : AgeUtil.calculateGrade(birthDate);
            
            // 급수명 조회
            String beltName = beltCode != null 
                    ? commonCodeRepository.findById(beltCode).map(CommonCode::getCodeName).orElse(beltCode)
                    : "";
            
            
            // 퇴관일 (퇴관 상태이고 deletedAt이 있을 경우)
            LocalDate exitDate = null;
            if ("퇴관".equals(statusCode) && deletedAtTs != null) {
                exitDate = deletedAtTs.toLocalDateTime().toLocalDate();
            }
            
            StudentListRespDto respDto = StudentListRespDto.builder()
                    .studentCode(studentCode)
                    .studentName(studentName)
                    .genderCode(genderCode)
                    .genderName(genderName)
                    .age(age)
                    .grade(grade)
                    .beltCode(beltCode)
                    .beltName(beltName)
                    .birthDate(birthDate)
                    .statusCode(statusCode)
                    .registDate(registDate)
                    .exitDate(exitDate)
                    .build();
            
            resultList.add(respDto);
        }
        
        log.info("DTO 변환 완료: {}건", resultList.size());
        
        // 5. 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("content", resultList);
        response.put("totalElements", totalElements);
        
        log.info("제자 목록 조회 완료: 총 {}명, 현재 페이지 {}명", totalElements, resultList.size());
        
        return resultList;
    }
    
    /**
     * 제자 기본정보 조회
     */
    public StudentBasicInfoRespDto getStudentBasicInfo(String studentCode) {
        
        log.info("제자 기본정보 조회 시작: studentCode={}", studentCode);
        
        // 1. 제자 기본 정보 조회
        Student student = studentRepository.findById(studentCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 제자입니다: " + studentCode));
        
        // 2. 등록목적 조회 (다중)
        List<StudentPurpose> purposes = studentPurposeRepository.findByStudentCode(studentCode);
        List<String> purposeCodes = new ArrayList<>();
        String purposeEtcValue = null;
        
        for (StudentPurpose purpose : purposes) {
            purposeCodes.add(purpose.getPurposeCode());
            if ("PURPOSE_009".equals(purpose.getPurposeCode())) {
                purposeEtcValue = purpose.getEtcValue();
            }
        }
        
        // 3. 태권도 경력 조회 (student_belt 테이블에서 가장 최근 것)
        Integer taekwondoMonths = null;
        List<StudentBelt> beltHistory = studentBeltRepository.findByStudentCode(studentCode);
        if (!beltHistory.isEmpty()) {
            // created_at 기준으로 최신 것 찾기
            StudentBelt latestBelt = beltHistory.stream()
                    .max((b1, b2) -> b1.getCreatedAt().compareTo(b2.getCreatedAt()))
                    .orElse(null);
            if (latestBelt != null) {
                taekwondoMonths = latestBelt.getTaekwondoMonths();
            }
        }
        
        // 4. 보호자 정보 조회 (주 보호자만)
        StudentGuardian studentGuardian = studentGuardianRepository.findByStudentCode(studentCode)
                .stream()
                .findFirst()
                .orElse(null);
        
        StudentBasicInfoRespDto.GuardianInfo guardianInfo = null;
        
        if (studentGuardian != null) {
            Guardian guardian = guardianRepository.findById(studentGuardian.getGuardianCode())
                    .orElse(null);
            
            if (guardian != null) {
                guardianInfo = StudentBasicInfoRespDto.GuardianInfo.builder()
                        .guardianCode(guardian.getGuardianCode())
                        .guardianName(guardian.getGuardianName())
                        .relationship(studentGuardian.getRelationship())
                        .guardianPhone(guardian.getGuardianPhone())
                        .guardianBirthDate(guardian.getGuardianBirthDate())
                        .build();
            }
        }
        
        // 5. 응답 DTO 생성
        StudentBasicInfoRespDto respDto = StudentBasicInfoRespDto.builder()
                .studentCode(student.getStudentCode())
                .studentName(student.getStudentName())
                .studentNameEn(student.getStudentNameEn())
                .registDate(student.getRegistDate())
                .statusCode(student.getStatusCode())
                .beltCode(student.getBeltCode())
                .profileImage(student.getProfileImageUrl())  // profileImageUrl로 수정
                .birthDate(student.getBirthDate())
                .genderCode(student.getGenderCode())
                .genderName(student.getGenderName())
                .studentPhone(student.getStudentPhone())
                .studentZipcode(student.getStudentZipcode())
                .studentAdd(student.getStudentAdd())
                .studentAdd2(student.getStudentAdd2())
                .schoolName(student.getSchoolName())
                .grade(student.getGrade())
                .className(student.getClassName())
                .registPathCode(student.getRegistPathCode())
                .purposeCodes(purposeCodes)
                .purposeEtcValue(purposeEtcValue)
                .registReason(student.getRegistReason())
                .hasExerciseHistory(student.getHasExerciseHistory())
                .taekwondoMonths(taekwondoMonths)  // student_belt에서 가져온 값
                .previousSports(student.getPreviousSports())
                .previousDojangExp(student.getPreviousDojangExp())
                .hasMedication(student.getHasMedication())
                .hasAllergy(student.getHasAllergy())
                .healthNote(student.getHealthNote())
                .guardian(guardianInfo)
                .build();
        
        log.info("제자 기본정보 조회 완료: {}", studentCode);
        
        return respDto;
    }
    
    /**
     * 제자 기본정보 수정
     */
    @Transactional
    public void updateStudentBasicInfo(String dojangCode, UpdateStudentBasicInfoReqDto reqDto) {
        
        log.info("제자 기본정보 수정 시작: studentCode={}", reqDto.getStudentCode());
        
        // 1. 제자 조회
        Student student = studentRepository.findById(reqDto.getStudentCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 제자입니다: " + reqDto.getStudentCode()));
        
        // 2. 급수 또는 경력 변경 감지 및 이력 추가
        // 최신 급수 이력에서 현재 경력 조회
        StudentBelt latestBelt = studentBeltRepository
                .findTopByStudentCodeOrderByCreatedAtDesc(reqDto.getStudentCode())
                .orElse(null);
        
        Integer currentMonths = latestBelt != null ? latestBelt.getTaekwondoMonths() : null;
        Integer newMonths = reqDto.getTaekwondoMonths();
        
        boolean beltChanged = !student.getBeltCode().equals(reqDto.getBeltCode());
        boolean monthsChanged = !java.util.Objects.equals(currentMonths, newMonths);
        
        // 급수 또는 경력이 변경되었으면 이력 추가
        if (beltChanged || monthsChanged) {
            String beltHistoryCode = generateBeltHistoryCode(dojangCode);
            StudentBelt studentBelt = StudentBelt.builder()
                    .beltHistoryCode(beltHistoryCode)
                    .studentCode(reqDto.getStudentCode())
                    .beltCode(reqDto.getBeltCode())
                    .taekwondoMonths(newMonths != null ? newMonths : 0)
                    .acquiredAt(LocalDate.now())
                    .build();
            studentBeltRepository.save(studentBelt);
            log.info("급수/경력 변경 이력 추가: 급수[{} → {}], 경력[{}개월 → {}개월]", 
                    student.getBeltCode(), reqDto.getBeltCode(), currentMonths, newMonths);
        }
        
        // 3. 재원상태 변경 감지 및 이력 추가
        boolean statusChanged = !student.getStatusCode().equals(reqDto.getStatusCode());
        if (statusChanged) {
            String statusHistoryCode = generateStatusHistoryCode(dojangCode);
            LocalDate changeDate = reqDto.getStatusChangeDate() != null ? reqDto.getStatusChangeDate() : LocalDate.now();
            
            StudentStatus studentStatus = StudentStatus.builder()
                    .statusHistoryCode(statusHistoryCode)
                    .studentCode(reqDto.getStudentCode())
                    .statusCode(reqDto.getStatusCode())
                    .changeDate(changeDate)
                    .statusReason(null)  // 사유 없이 저장
                    .build();
            studentStatusRepository.save(studentStatus);
            log.info("재원상태 변경 이력 추가: {} → {}", student.getStatusCode(), reqDto.getStatusCode());
            
            // 퇴관 처리
            if ("퇴관".equals(reqDto.getStatusCode())) {
                handleStudentWithdrawal(reqDto.getStudentCode());
            }
        }
        
        // 4. 제자 기본 정보 업데이트
        student.setStudentName(reqDto.getStudentName());
        student.setStudentNameEn(reqDto.getStudentNameEn());
        student.setRegistDate(reqDto.getRegistDate());
        student.setStatusCode(reqDto.getStatusCode());
        student.setBeltCode(reqDto.getBeltCode());
        student.setProfileImageUrl(reqDto.getProfileImage());
        
        student.setBirthDate(reqDto.getBirthDate());
        student.setGenderCode(reqDto.getGenderCode());
        student.setGenderName(reqDto.getGenderName());
        student.setStudentPhone(reqDto.getStudentPhone());
        
        student.setStudentZipcode(reqDto.getStudentZipcode());
        student.setStudentAdd(reqDto.getStudentAdd());
        student.setStudentAdd2(reqDto.getStudentAdd2());
        
        student.setSchoolName(reqDto.getSchoolName());
        student.setGrade(reqDto.getGrade());
        student.setClassName(reqDto.getClassName());
        
        student.setRegistPathCode(reqDto.getRegistPathCode());
        student.setRegistReason(reqDto.getRegistReason());
        
        student.setHasExerciseHistory(reqDto.getHasExerciseHistory());
        student.setPreviousSports(reqDto.getPreviousSports());
        student.setPreviousDojangExp(reqDto.getPreviousDojangExp());
        
        student.setHasMedication(reqDto.getHasMedication());
        student.setHasAllergy(reqDto.getHasAllergy());
        student.setHealthNote(reqDto.getHealthNote());
        
        studentRepository.save(student);
        
        // 5. 등록목적 업데이트 (기존 삭제 후 재등록)
        List<StudentPurpose> existingPurposes = studentPurposeRepository.findByStudentCode(reqDto.getStudentCode());
        studentPurposeRepository.deleteAll(existingPurposes);
        
        if (reqDto.getPurposeCodes() != null && !reqDto.getPurposeCodes().isEmpty()) {
            for (String purposeCode : reqDto.getPurposeCodes()) {
                String etcValue = "PURPOSE_999".equals(purposeCode) ? reqDto.getPurposeEtcValue() : null;
                
                StudentPurpose studentPurpose = StudentPurpose.builder()
                        .studentCode(reqDto.getStudentCode())
                        .purposeCode(purposeCode)
                        .etcValue(etcValue)
                        .build();
                studentPurposeRepository.save(studentPurpose);
            }
        }
        
        log.info("제자 기본정보 수정 완료: {}", reqDto.getStudentCode());
    }
    
    /**
     * 퇴관 처리
     * 1. student_mst 삭제 플래그 설정
     * 2. 미납 청구서 삭제 (현재월 이후)
     * 3. 제자수련정보 삭제
     * 4. 제자수업정보 삭제
     * 5. 제자교육비정보 삭제
     */
    private void handleStudentWithdrawal(String studentCode) {
        log.info("퇴관 처리 시작: studentCode={}", studentCode);
        
        // 1. student_mst 삭제 플래그 설정
        Student student = studentRepository.findById(studentCode).orElseThrow();
        student.setIsDeleted(1);
        student.setDeletedAt(LocalDateTime.now());
        studentRepository.save(student);
        log.info("제자 삭제 플래그 설정 완료");
        
        // 2. 미납 청구서 삭제 (현재월 포함 이후 청구서만)
        // monthly_billing 테이블: billing_status='미납' AND billing_month >= 현재월
        int deletedBillings = monthlyBillingRepository.deleteUnpaidFutureBillings(studentCode);
        log.info("미납 청구서 삭제 완료: {}건", deletedBillings);
        
        // 3. 제자수련정보 삭제 (student_training)
        int deletedTrainings = studentTrainingRepository.deleteByStudentCode(studentCode);
        log.info("제자 수련정보 삭제 완료: {}건", deletedTrainings);
        
        // 4. 제자수업정보 삭제 (student_class)
        int deletedClasses = studentClassRepository.deleteByStudentCode(studentCode);
        log.info("제자 수업정보 삭제 완료: {}건", deletedClasses);
        
        // 5. 제자교육비정보 삭제 (student_tuition)
        int deletedTuitions = studentTuitionRepository.deleteByStudentCode(studentCode);
        log.info("제자 교육비정보 삭제 완료: {}건", deletedTuitions);
        
        log.info("퇴관 처리 완료: studentCode={}", studentCode);
    }
    
    /**
     * 제자 성향 정보 조회
     */
    public CharacterTraitInfoRespDto getCharacterTraitInfo(String studentCode) {
        
        log.info("제자 성향 정보 조회 시작: studentCode={}", studentCode);
        
        // 1. 제자 존재 여부 확인
        if (!studentRepository.existsById(studentCode)) {
            throw new IllegalArgumentException("존재하지 않는 제자입니다: " + studentCode);
        }
        
        // 2. 각 테이블에서 조회 후 trait_code 추출
        
        // 2-1. 성향_기본 (student_character)
        List<StudentCharacter> characterList = studentCharacterRepository.findByStudentCode(studentCode);
        List<String> personalityBasic = characterList.isEmpty() ? null : 
            characterList.stream().map(StudentCharacter::getCharacterCode).collect(Collectors.toList());
        String personalityBasicEtc = characterList.stream()
            .filter(e -> e.getCharacterCode().contains("999"))
            .findFirst()
            .map(StudentCharacter::getEtcValue)
            .orElse(null);
        
        // 2-2. 성향_정서 (student_emotion)
        List<StudentEmotion> emotionList = studentEmotionRepository.findByStudentCode(studentCode);
        List<String> emotion = emotionList.isEmpty() ? null :
            emotionList.stream().map(StudentEmotion::getEmotionCode).collect(Collectors.toList());
        String emotionEtc = emotionList.stream()
            .filter(e -> e.getEmotionCode().contains("999"))
            .findFirst()
            .map(StudentEmotion::getEtcValue)
            .orElse(null);
        
        // 2-3. 성향_사회성 (student_social)
        List<StudentSocial> socialList = studentSocialRepository.findByStudentCode(studentCode);
        List<String> sociability = socialList.isEmpty() ? null :
            socialList.stream().map(StudentSocial::getSocialCode).collect(Collectors.toList());
        String sociabilityEtc = socialList.stream()
            .filter(e -> e.getSocialCode().contains("999"))
            .findFirst()
            .map(StudentSocial::getEtcValue)
            .orElse(null);
        
        // 2-4. 성향_수업반응 (student_class_response)
        List<StudentClassResponse> classResponseList = studentClassResponseRepository.findByStudentCode(studentCode);
        List<String> lessonResponse = classResponseList.isEmpty() ? null :
            classResponseList.stream().map(StudentClassResponse::getClassResponseCode).collect(Collectors.toList());
        String lessonResponseEtc = classResponseList.stream()
            .filter(e -> e.getClassResponseCode().contains("999"))
            .findFirst()
            .map(StudentClassResponse::getEtcValue)
            .orElse(null);
        
        // 2-5. 건강특성 (student_health)
        List<StudentHealth> healthList = studentHealthRepository.findByStudentCode(studentCode);
        List<String> healthTrait = healthList.isEmpty() ? null :
            healthList.stream().map(StudentHealth::getHealthCode).collect(Collectors.toList());
        String healthTraitEtc = healthList.stream()
            .filter(e -> e.getHealthCode().contains("999"))
            .findFirst()
            .map(StudentHealth::getEtcValue)
            .orElse(null);
        
        // 2-6. 체형특성 (student_body_type)
        List<StudentBodyType> bodyTypeList = studentBodyTypeRepository.findByStudentCode(studentCode);
        List<String> bodyTrait = bodyTypeList.isEmpty() ? null :
            bodyTypeList.stream().map(StudentBodyType::getBodyTypeCode).collect(Collectors.toList());
        String bodyTraitEtc = bodyTypeList.stream()
            .filter(e -> e.getBodyTypeCode().contains("999"))
            .findFirst()
            .map(StudentBodyType::getEtcValue)
            .orElse(null);
        
        // 2-7. 신체민감사항 (student_body_part)
        List<StudentBodyPart> bodyPartList = studentBodyPartRepository.findByStudentCode(studentCode);
        List<String> bodySensitive = bodyPartList.isEmpty() ? null :
            bodyPartList.stream().map(StudentBodyPart::getBodyPartCode).collect(Collectors.toList());
        String bodySensitiveEtc = bodyPartList.stream()
            .filter(e -> e.getBodyPartCode().contains("999"))
            .findFirst()
            .map(StudentBodyPart::getEtcValue)
            .orElse(null);
        
        // 2-8. 변화필요부분 (student_improvement)
        List<StudentImprovement> improvementList = studentImprovementRepository.findByStudentCode(studentCode);
        List<String> changeNeed = improvementList.isEmpty() ? null :
            improvementList.stream().map(StudentImprovement::getImprovementCode).collect(Collectors.toList());
        String changeNeedEtc = improvementList.stream()
            .filter(e -> e.getImprovementCode().contains("999"))
            .findFirst()
            .map(StudentImprovement::getEtcValue)
            .orElse(null);
        
        // 2-9. 강점 (student_strength)
        List<StudentStrength> strengthList = studentStrengthRepository.findByStudentCode(studentCode);
        List<String> strength = strengthList.isEmpty() ? null :
            strengthList.stream().map(StudentStrength::getStrengthCode).collect(Collectors.toList());
        String strengthEtc = strengthList.stream()
            .filter(e -> e.getStrengthCode().contains("999"))
            .findFirst()
            .map(StudentStrength::getEtcValue)
            .orElse(null);
        
        // 3. DTO 생성
        CharacterTraitInfoRespDto respDto = CharacterTraitInfoRespDto.builder()
                .studentCode(studentCode)
                .personalityBasic(personalityBasic)
                .personalityBasicEtc(personalityBasicEtc)
                .emotion(emotion)
                .emotionEtc(emotionEtc)
                .sociability(sociability)
                .sociabilityEtc(sociabilityEtc)
                .lessonResponse(lessonResponse)
                .lessonResponseEtc(lessonResponseEtc)
                .healthTrait(healthTrait)
                .healthTraitEtc(healthTraitEtc)
                .bodyTrait(bodyTrait)
                .bodyTraitEtc(bodyTraitEtc)
                .bodySensitive(bodySensitive)
                .bodySensitiveEtc(bodySensitiveEtc)
                .changeNeed(changeNeed)
                .changeNeedEtc(changeNeedEtc)
                .strength(strength)
                .strengthEtc(strengthEtc)
                .build();
        
        log.info("제자 성향 정보 조회 완료: studentCode={}", studentCode);
        
        return respDto;
    }
    
    /**
     * 제자 성향 정보 저장
     * 기존 데이터 전체 삭제 후 새로 INSERT
     */
    @Transactional
    public void saveCharacterTraitInfo(SaveCharacterTraitReqDto reqDto) {
        
        String studentCode = reqDto.getStudentCode();
        log.info("제자 성향 정보 저장 시작: studentCode={}", studentCode);
        
        // 1. 제자 존재 여부 확인
        if (!studentRepository.existsById(studentCode)) {
            throw new IllegalArgumentException("존재하지 않는 제자입니다: " + studentCode);
        }
        
        // 2. 기존 데이터 전체 삭제 (9개 테이블)
        studentCharacterRepository.deleteByStudentCode(studentCode);
        studentEmotionRepository.deleteByStudentCode(studentCode);
        studentSocialRepository.deleteByStudentCode(studentCode);
        studentClassResponseRepository.deleteByStudentCode(studentCode);
        studentHealthRepository.deleteByStudentCode(studentCode);
        studentBodyTypeRepository.deleteByStudentCode(studentCode);
        studentBodyPartRepository.deleteByStudentCode(studentCode);
        studentImprovementRepository.deleteByStudentCode(studentCode);
        studentStrengthRepository.deleteByStudentCode(studentCode);
        
        log.info("기존 성향 데이터 삭제 완료: studentCode={}", studentCode);
        
        // 3. 새 데이터 INSERT (9개 카테고리)
        
        // 3-1. 성향_기본 (student_character)
        if (reqDto.getPersonalityBasic() != null && !reqDto.getPersonalityBasic().isEmpty()) {
            for (String code : reqDto.getPersonalityBasic()) {
                StudentCharacter entity = StudentCharacter.builder()
                        .studentCode(studentCode)
                        .characterCode(code)
                        .etcValue(code.contains("999") ? reqDto.getPersonalityBasicEtc() : null)
                        .build();
                studentCharacterRepository.save(entity);
            }
        }
        
        // 3-2. 성향_정서 (student_emotion)
        if (reqDto.getEmotion() != null && !reqDto.getEmotion().isEmpty()) {
            for (String code : reqDto.getEmotion()) {
                StudentEmotion entity = StudentEmotion.builder()
                        .studentCode(studentCode)
                        .emotionCode(code)
                        .etcValue(code.contains("999") ? reqDto.getEmotionEtc() : null)
                        .build();
                studentEmotionRepository.save(entity);
            }
        }
        
        // 3-3. 성향_사회성 (student_social)
        if (reqDto.getSociability() != null && !reqDto.getSociability().isEmpty()) {
            for (String code : reqDto.getSociability()) {
                StudentSocial entity = StudentSocial.builder()
                        .studentCode(studentCode)
                        .socialCode(code)
                        .etcValue(code.contains("999") ? reqDto.getSociabilityEtc() : null)
                        .build();
                studentSocialRepository.save(entity);
            }
        }
        
        // 3-4. 성향_수업반응 (student_class_response)
        if (reqDto.getLessonResponse() != null && !reqDto.getLessonResponse().isEmpty()) {
            for (String code : reqDto.getLessonResponse()) {
                StudentClassResponse entity = StudentClassResponse.builder()
                        .studentCode(studentCode)
                        .classResponseCode(code)
                        .etcValue(code.contains("999") ? reqDto.getLessonResponseEtc() : null)
                        .build();
                studentClassResponseRepository.save(entity);
            }
        }
        
        // 3-5. 건강특성 (student_health)
        if (reqDto.getHealthTrait() != null && !reqDto.getHealthTrait().isEmpty()) {
            for (String code : reqDto.getHealthTrait()) {
                StudentHealth entity = StudentHealth.builder()
                        .studentCode(studentCode)
                        .healthCode(code)
                        .etcValue(code.contains("999") ? reqDto.getHealthTraitEtc() : null)
                        .build();
                studentHealthRepository.save(entity);
            }
        }
        
        // 3-6. 체형특성 (student_body_type)
        if (reqDto.getBodyTrait() != null && !reqDto.getBodyTrait().isEmpty()) {
            for (String code : reqDto.getBodyTrait()) {
                StudentBodyType entity = StudentBodyType.builder()
                        .studentCode(studentCode)
                        .bodyTypeCode(code)
                        .etcValue(code.contains("999") ? reqDto.getBodyTraitEtc() : null)
                        .build();
                studentBodyTypeRepository.save(entity);
            }
        }
        
        // 3-7. 신체민감사항 (student_body_part)
        if (reqDto.getBodySensitive() != null && !reqDto.getBodySensitive().isEmpty()) {
            for (String code : reqDto.getBodySensitive()) {
                StudentBodyPart entity = StudentBodyPart.builder()
                        .studentCode(studentCode)
                        .bodyPartCode(code)
                        .etcValue(code.contains("999") ? reqDto.getBodySensitiveEtc() : null)
                        .build();
                studentBodyPartRepository.save(entity);
            }
        }
        
        // 3-8. 변화필요부분 (student_improvement)
        if (reqDto.getChangeNeed() != null && !reqDto.getChangeNeed().isEmpty()) {
            for (String code : reqDto.getChangeNeed()) {
                StudentImprovement entity = StudentImprovement.builder()
                        .studentCode(studentCode)
                        .improvementCode(code)
                        .etcValue(code.contains("999") ? reqDto.getChangeNeedEtc() : null)
                        .build();
                studentImprovementRepository.save(entity);
            }
        }
        
        // 3-9. 강점 (student_strength)
        if (reqDto.getStrength() != null && !reqDto.getStrength().isEmpty()) {
            for (String code : reqDto.getStrength()) {
                StudentStrength entity = StudentStrength.builder()
                        .studentCode(studentCode)
                        .strengthCode(code)
                        .etcValue(code.contains("999") ? reqDto.getStrengthEtc() : null)
                        .build();
                studentStrengthRepository.save(entity);
            }
        }
        
        log.info("제자 성향 정보 저장 완료: studentCode={}", studentCode);
    }
    
    /**
     * 급수/경력 이력 조회
     */
    public BeltHistoryListRespDto getBeltHistory(String studentCode) {
        
        log.info("급수/경력 이력 조회 시작: studentCode={}", studentCode);
        
        // 1. 제자 조회 (현재 급수 가져오기)
        Student student = studentRepository.findById(studentCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 제자입니다: " + studentCode));
        
        // 2. 이력 목록 조회 (최신순)
        List<StudentBelt> beltHistories = studentBeltRepository
                .findByStudentCodeOrderByAcquiredAtDesc(studentCode);
        
        // 3. 모든 급수 코드 수집
        Set<String> beltCodes = beltHistories.stream()
                .map(StudentBelt::getBeltCode)
                .collect(Collectors.toSet());
        if (student.getBeltCode() != null) {
            beltCodes.add(student.getBeltCode());
        }
        
        // 4. 급수명 조회 (한 번에)
        Map<String, String> beltNameMap = new HashMap<>();
        if (!beltCodes.isEmpty()) {
            List<CommonCode> commonCodes = commonCodeRepository
                    .findByGroupCodeAndCommonCodeIn("BELT", new ArrayList<>(beltCodes));
            
            beltNameMap = commonCodes.stream()
                    .collect(Collectors.toMap(
                        CommonCode::getCommonCode,
                        CommonCode::getCodeName
                    ));
        }
        
        // 5. DTO 변환
        final Map<String, String> finalBeltNameMap = beltNameMap;
        List<BeltHistoryItemDto> histories = beltHistories.stream()
                .map((StudentBelt history) -> BeltHistoryItemDto.builder()
                        .historyCode(history.getBeltHistoryCode())
                        .beltCode(history.getBeltCode())
                        .beltName(finalBeltNameMap.getOrDefault(history.getBeltCode(), ""))
                        .careerMonths(history.getTaekwondoMonths())
                        .changeDate(history.getAcquiredAt())
                        .build())
                .collect(Collectors.toList());
        
        // 6. 현재 경력 = 가장 최신 이력의 경력개월수
        int currentCareer = beltHistories.isEmpty() ? 0 : beltHistories.get(0).getTaekwondoMonths();
        
        // 7. 현재 급수명
        String currentBeltName = finalBeltNameMap.getOrDefault(student.getBeltCode(), "");
        
        log.info("급수/경력 이력 조회 완료: studentCode={}, 이력 건수={}", studentCode, histories.size());
        
        return BeltHistoryListRespDto.builder()
                .currentBelt(student.getBeltCode())
                .currentBeltName(currentBeltName)
                .currentCareer(currentCareer)
                .histories(histories)
                .build();
    }
     
    /**
     * 급수/경력 이력 등록
     */
    @Transactional
    public void createBeltHistory(CreateBeltHistoryReqDto reqDto, String dojangCode) {
        
        log.info("급수/경력 이력 등록 시작: studentCode={}", reqDto.getStudentCode());
        
        // 1. 제자 존재 확인
        Student student = studentRepository.findById(reqDto.getStudentCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 제자입니다: " + reqDto.getStudentCode()));
        
        // 2. historyCode 자동 생성 (도장코드-BH{YYnnn})
        String historyCode = generateBeltHistoryCode(dojangCode);
        
        // 3. StudentBelt 이력 추가
        StudentBelt studentBelt = StudentBelt.builder()
                .beltHistoryCode(historyCode)
                .studentCode(reqDto.getStudentCode())
                .beltCode(reqDto.getBeltCode())
                .taekwondoMonths(reqDto.getCareerMonths())
                .acquiredAt(reqDto.getChangeDate())
                .build();
        
        studentBeltRepository.save(studentBelt);
        
        // 4. Student 테이블의 현재 급수 업데이트
        student.setBeltCode(reqDto.getBeltCode());
        studentRepository.save(student);
        
        log.info("급수/경력 이력 등록 완료: historyCode={}", historyCode);
    }
     
    /**
     * 급수/경력 이력 삭제
     */
    @Transactional
    public void deleteBeltHistory(String historyCode) {
        
        log.info("급수/경력 이력 삭제 시작: historyCode={}", historyCode);
        
        // 1. 이력 존재 확인
        StudentBelt studentBelt = studentBeltRepository.findById(historyCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이력입니다: " + historyCode));
        
        // 2. 이력 삭제
        studentBeltRepository.delete(studentBelt);
        
        log.info("급수/경력 이력 삭제 완료: historyCode={}", historyCode);
    }
    
}