package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.management.reqDto.BatchSaveEducationManagementReqDto;
import com.taekwondo.miwool.dto.management.reqDto.EducationItemChangeDto;
import com.taekwondo.miwool.dto.management.reqDto.SaveEducationDetailReqDto;
import com.taekwondo.miwool.dto.management.reqDto.StudentEducationChangeDto;
import com.taekwondo.miwool.dto.management.respDto.EducationDetailItemDto;
import com.taekwondo.miwool.dto.management.respDto.EducationDetailRespDto;
import com.taekwondo.miwool.dto.management.respDto.EducationManagementItemDto;
import com.taekwondo.miwool.entity.Student;
import com.taekwondo.miwool.entity.StudentManagement;
import com.taekwondo.miwool.repository.StudentManagementRepository;
import com.taekwondo.miwool.repository.StudentRepository;
import com.taekwondo.miwool.util.AgeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EducationManagementService {
    
    private final StudentManagementRepository studentManagementRepository;
    private final StudentRepository studentRepository;
    
    /**
     * 교육지도 목록 조회
     */
    public List<EducationManagementItemDto> getEducationManagementList(
            String yearMonth,
            String studentSearch,
            String beltCode,
            Integer genderCode,
            String grade,
            String status,
            String category,
            String dojangCode) {
        
        log.info("교육지도 목록 조회 시작: yearMonth={}, status={}, category={}", 
                yearMonth, status, category);
        
        // Repository에서 Native Query로 조회
        List<Object[]> results = studentManagementRepository.findEducationManagementList(
                yearMonth,
                studentSearch,
                beltCode,
                genderCode,
                grade,
                status,
                category,
                dojangCode
        );
        
        // DTO 변환
        List<EducationManagementItemDto> items = results.stream()
                .map(row -> {
                    String studentCode = (String) row[0];
                    int genderCodeVal = (int) row[1];
                    String studentName = (String) row[2];
                    
                    // java.sql.Date → LocalDate 변환
                    java.sql.Date sqlDate = (java.sql.Date) row[3];
                    LocalDate birthDate = sqlDate.toLocalDate();
                    
                    String gradeVal = (String) row[4];
                    
                    // boolean 변환 (1 = true, 0 = false)
                    boolean phoneYn = ((Number) row[5]).intValue() > 0;
                    boolean messageYn = ((Number) row[6]).intValue() > 0;
                    boolean letterYn = ((Number) row[7]).intValue() > 0;
                    boolean snackYn = ((Number) row[8]).intValue() > 0;
                    boolean videoYn = ((Number) row[9]).intValue() > 0;
                    boolean observationYn = ((Number) row[10]).intValue() > 0;
                    boolean etcYn = ((Number) row[11]).intValue() > 0;
                    String etcContent = (String) row[12];
                    
                    // 나이 계산
                    int age = AgeUtil.calculateKoreanAge(birthDate);
                    
                    return EducationManagementItemDto.builder()
                            .studentCode(studentCode)
                            .genderCode(genderCodeVal)
                            .studentName(studentName)
                            .age(age)
                            .grade(gradeVal)
                            .phoneYn(phoneYn)
                            .messageYn(messageYn)
                            .letterYn(letterYn)
                            .snackYn(snackYn)
                            .videoYn(videoYn)
                            .observationYn(observationYn)
                            .etcYn(etcYn)
                            .etcContent(etcContent)
                            .build();
                })
                .collect(Collectors.toList());
        
        // status + category 필터링
        if (status != null || category != null) {
            items = items.stream()
                    .filter(item -> matchesStatusAndCategory(item, status, category))
                    .collect(Collectors.toList());
        }
        
        log.info("교육지도 목록 조회 완료: 조회 건수={}", items.size());
        
        return items;
    }
    
    /**
     * status + category 조합 필터링
     */
    private boolean matchesStatusAndCategory(EducationManagementItemDto item, String status, String category) {
        // status=null, category=null → true (전체)
        if (status == null && category == null) {
            return true;
        }
        
        // status=실시, category=null → 모든 항목 실시
        if ("실시".equals(status) && category == null) {
            return item.isPhoneYn() && item.isMessageYn() && item.isLetterYn() 
                && item.isSnackYn() && item.isVideoYn() && item.isObservationYn() && item.isEtcYn();
        }
        
        // status=실시, category=특정항목 → 해당 항목 실시
        if ("실시".equals(status)) {
            return checkCategory(item, category, true);
        }
        
        // status=미실시, category=null → 모든 항목 미실시
        if ("미실시".equals(status) && category == null) {
            return !item.isPhoneYn() && !item.isMessageYn() && !item.isLetterYn() 
                && !item.isSnackYn() && !item.isVideoYn() && !item.isObservationYn() && !item.isEtcYn();
        }
        
        // status=미실시, category=특정항목 → 해당 항목 미실시
        if ("미실시".equals(status)) {
            return checkCategory(item, category, false);
        }
        
        return true;
    }
    
    /**
     * 특정 카테고리 체크
     */
    private boolean checkCategory(EducationManagementItemDto item, String category, boolean expected) {
        if (category == null) {
            return true;
        }
        
        switch (category) {
            case "전화": return item.isPhoneYn() == expected;
            case "문자": return item.isMessageYn() == expected;
            case "손편지": return item.isLetterYn() == expected;
            case "간식": return item.isSnackYn() == expected;
            case "영상": return item.isVideoYn() == expected;
            case "관찰지": return item.isObservationYn() == expected;
            case "기타": return item.isEtcYn() == expected;
            default: return true;
        }
    }
    
    /**
     * 교육지도 일괄 저장 (변경된 것만)
     * true → 없으면 INSERT, 있으면 유지
     * false → 있으면 DELETE, 없으면 무시
     */
    @Transactional
    public void batchSaveEducationManagement(
            BatchSaveEducationManagementReqDto reqDto,
            String dojangCode) {
        
        log.info("교육지도 일괄 저장 시작: month={}, 학생 수={}", 
                reqDto.getMonth(), reqDto.getStudents().size());
        
        String yearMonth = reqDto.getMonth(); // "2026-04"
        
        // executed_date 결정: 현재 월이면 현재 날짜, 과거 월이면 1일
        LocalDate executedDate = determineExecutedDate(yearMonth);
        log.info("executed_date: {}", executedDate);
        
        for (StudentEducationChangeDto student : reqDto.getStudents()) {
            
            log.info("===== 학생 처리 시작: {} =====", student.getStudentCode());
            
            // 1. 기존 데이터 조회 (항목코드 → Entity)
            List<StudentManagement> existing = studentManagementRepository
                    .findByStudentCodeAndYearMonth(student.getStudentCode(), yearMonth);
            
            log.info("기존 데이터 조회 결과: {} 건", existing.size());
            for (StudentManagement sm : existing) {
                log.info("  - management_type_code: [{}], executed_date: {}", 
                        sm.getManagementTypeCode(), sm.getExecutedDate());
            }
            
            Map<String, StudentManagement> existingMap = existing.stream()
                    .collect(Collectors.toMap(
                        StudentManagement::getManagementTypeCode,
                        Function.identity()
                    ));
            
            log.info("existingMap keys: {}", existingMap.keySet());
            
            // 2. 변경된 항목별 처리
            Map<String, Boolean> changes = student.getChanges();
            log.info("변경 요청: {}", changes);
            
            for (Map.Entry<String, Boolean> entry : changes.entrySet()) {
                String itemKey = entry.getKey();           // "letter"
                Boolean checked = entry.getValue();        // true/false
                String typeCode = mapToTypeCode(itemKey);  // "손편지"
                
                log.info(">>> 처리: itemKey=[{}], checked={}, typeCode=[{}]", itemKey, checked, typeCode);
                
                if (typeCode == null) {
                    log.info("    typeCode가 null이므로 스킵");
                    continue;
                }
                
                if (checked) {
                    // true → 없으면 INSERT, 있으면 유지
                    boolean exists = existingMap.containsKey(typeCode);
                    log.info("    existingMap.containsKey([{}]) = {}", typeCode, exists);
                    
                    if (!exists) {
                        log.info("    → INSERT 실행 (executed_date: {})", executedDate);
                        // etc인 경우 etcContent 가져오기
                        String note = "etc".equals(itemKey) ? student.getEtcContent() : null;
                        saveEducationItem(dojangCode, student.getStudentCode(), typeCode, executedDate, note);
                    } else {
                        log.info("    → 이미 존재하므로 유지 (executed_date 보존)");
                    }
                    
                } else {
                    // false → 있으면 DELETE, 없으면 무시
                    boolean exists = existingMap.containsKey(typeCode);
                    log.info("    existingMap.containsKey([{}]) = {}", typeCode, exists);
                    
                    if (exists) {
                        log.info("    → DELETE 실행");
                        studentManagementRepository.delete(existingMap.get(typeCode));
                    } else {
                        log.info("    → 존재하지 않으므로 무시");
                    }
                }
            }
            
            log.info("===== 학생 처리 완료: {} =====\n", student.getStudentCode());
        }
        
        log.info("교육지도 일괄 저장 완료: 학생 수={}", reqDto.getStudents().size());
    }
    
    /**
     * executed_date 결정
     * - 현재 월이면: 현재 날짜 (예: 2026-04-17)
     * - 과거 월이면: 해당 월의 1일 (예: 2026-03-01)
     */
    private LocalDate determineExecutedDate(String yearMonth) {
        LocalDate now = LocalDate.now();
        String currentYearMonth = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        
        if (yearMonth.equals(currentYearMonth)) {
            // 현재 월이면 현재 날짜
            return now;
        } else {
            // 과거 월이면 해당 월의 1일
            return LocalDate.parse(yearMonth + "-01");
        }
    }
    
    /**
     * 항목명 → 관리유형코드 변환
     */
    private String mapToTypeCode(String itemKey) {
        switch (itemKey) {
            case "phone": return "전화";
            case "message": return "문자";
            case "letter": return "손편지";
            case "snack": return "간식";
            case "video": return "영상";
            case "observation": return "관찰지";
            case "etc": return "기타";
            default: return null; // 알 수 없는 항목 무시
        }
    }
    
    /**
     * 교육 항목 저장
     */
    private void saveEducationItem(
            String dojangCode,
            String studentCode,
            String managementTypeCode,
            LocalDate executedDate,
            String note) {
        
        // managementCode 자동 생성
        String managementCode = generateManagementCode(dojangCode);
        
        StudentManagement entity = StudentManagement.builder()
                .managementCode(managementCode)
                .studentCode(studentCode)
                .managementTypeCode(managementTypeCode)
                .executedDate(executedDate)
                .note(note)
                .build();
        
        studentManagementRepository.save(entity);
    }
    
    /**
     * managementCode 자동 생성
     * 형식: MW26001-MG24001 (MG = ManaGement)
     */
    private String generateManagementCode(String dojangCode) {
        String year = String.valueOf(LocalDate.now().getYear()).substring(2);
        String prefix = dojangCode + "-MG" + year;
        
        return studentManagementRepository.findFirstByManagementCodeStartingWithOrderByManagementCodeDesc(prefix)
                .map(entity -> {
                    String lastCode = entity.getManagementCode();
                    int nextSeq = Integer.parseInt(lastCode.substring(lastCode.length() - 3)) + 1;
                    return prefix + String.format("%03d", nextSeq);
                })
                .orElse(prefix + "001");
    }
    
    /**
     * 교육지도 상세 조회
     * 7개 항목 모두 반환 (체크 여부 + 메모)
     */
    public EducationDetailRespDto getEducationDetail(
            String studentCode,
            String yearMonth) {
        
        log.info("교육지도 상세 조회: studentCode={}, yearMonth={}", studentCode, yearMonth);
        
        // 1. 제자 정보 조회
        Student student = studentRepository.findById(studentCode)
                .orElseThrow(() -> new IllegalArgumentException("제자를 찾을 수 없습니다: " + studentCode));
        
        // 2. 교육지도 데이터 조회
        List<StudentManagement> existing = studentManagementRepository
                .findByStudentCodeAndYearMonth(studentCode, yearMonth);
        
        Map<String, StudentManagement> existingMap = existing.stream()
                .collect(Collectors.toMap(
                    StudentManagement::getManagementTypeCode,
                    Function.identity()
                ));
        
        log.info("기존 데이터: {} 건", existing.size());
        
        // 3. 7개 항목 모두 빌드
        List<EducationDetailItemDto> items = new ArrayList<>();
        items.add(buildDetailItem("phone", "전화", existingMap));
        items.add(buildDetailItem("message", "문자", existingMap));
        items.add(buildDetailItem("letter", "손편지", existingMap));
        items.add(buildDetailItem("snack", "간식", existingMap));
        items.add(buildDetailItem("video", "영상", existingMap));
        items.add(buildDetailItem("observation", "관찰지", existingMap));
        items.add(buildDetailItem("etc", "기타", existingMap));
        
        // 4. 응답 생성
        return EducationDetailRespDto.builder()
                .studentCode(student.getStudentCode())
                .studentName(student.getStudentName())
                .genderCode(student.getGenderCode())
                .age(AgeUtil.calculateKoreanAge(student.getBirthDate()))
                .grade(student.getGrade())
                .yearMonth(yearMonth)
                .items(items)
                .build();
    }
    
    /**
     * 항목 빌드 헬퍼
     */
    private EducationDetailItemDto buildDetailItem(
            String type,
            String typeName,
            Map<String, StudentManagement> existingMap) {
        
        StudentManagement sm = existingMap.get(typeName);
        
        if (sm != null) {
            // 데이터 있음
            return EducationDetailItemDto.builder()
                    .type(type)
                    .typeName(typeName)
                    .checked(true)
                    .note(sm.getNote() != null ? sm.getNote() : "")
                    .build();
        } else {
            // 데이터 없음
            return EducationDetailItemDto.builder()
                    .type(type)
                    .typeName(typeName)
                    .checked(false)
                    .note("")
                    .build();
        }
    }
    
    /**
     * 교육지도 상세 저장 (변경된 것만)
     * - checked=false → DELETE
     * - checked=true && 데이터 없음 → INSERT
     * - checked=true && 데이터 있음 && note 변경됨 → UPDATE note + executed_date
     * - checked=true && 데이터 있음 && note 동일 → 패스
     */
    @Transactional
    public void saveEducationDetail(
            SaveEducationDetailReqDto reqDto,
            String dojangCode) {
        
        log.info("교육지도 상세 저장: studentCode={}, yearMonth={}", 
                reqDto.getStudentCode(), reqDto.getYearMonth());
        
        String studentCode = reqDto.getStudentCode();
        String yearMonth = reqDto.getYearMonth();
        
        // executed_date 결정
        LocalDate executedDate = determineExecutedDate(yearMonth);
        log.info("executed_date: {}", executedDate);
        
        // 1. 기존 데이터 조회
        List<StudentManagement> existing = studentManagementRepository
                .findByStudentCodeAndYearMonth(studentCode, yearMonth);
        
        Map<String, StudentManagement> existingMap = existing.stream()
                .collect(Collectors.toMap(
                    StudentManagement::getManagementTypeCode,
                    Function.identity()
                ));
        
        log.info("기존 데이터: {} 건, existingMap keys: {}", existing.size(), existingMap.keySet());
        
        // 2. 변경된 항목별 처리
        Map<String, EducationItemChangeDto> changes = reqDto.getChanges();
        log.info("변경 요청: {}", changes.keySet());
        
        for (Map.Entry<String, EducationItemChangeDto> entry : changes.entrySet()) {
            String itemKey = entry.getKey();                    // "letter"
            EducationItemChangeDto change = entry.getValue();   // {checked, note}
            String typeCode = mapToTypeCode(itemKey);           // "손편지"
            
            log.info(">>> 처리: itemKey=[{}], checked={}, note=[{}], typeCode=[{}]", 
                    itemKey, change.isChecked(), change.getNote(), typeCode);
            
            if (typeCode == null) {
                log.info("    typeCode가 null이므로 스킵");
                continue;
            }
            
            if (!change.isChecked()) {
                // false → DELETE
                if (existingMap.containsKey(typeCode)) {
                    log.info("    → DELETE 실행");
                    studentManagementRepository.delete(existingMap.get(typeCode));
                } else {
                    log.info("    → 존재하지 않으므로 무시");
                }
                
            } else {
                // true
                if (!existingMap.containsKey(typeCode)) {
                    // 데이터 없음 → INSERT
                    log.info("    → INSERT 실행 (executed_date: {})", executedDate);
                    saveEducationItem(dojangCode, studentCode, typeCode, executedDate, change.getNote());
                    
                } else {
                    // 데이터 있음 → note 비교
                    StudentManagement sm = existingMap.get(typeCode);
                    String oldNote = sm.getNote() != null ? sm.getNote() : "";
                    String newNote = change.getNote() != null ? change.getNote() : "";
                    
                    if (!oldNote.equals(newNote)) {
                        // note 변경됨 → UPDATE note + executed_date
                        log.info("    → UPDATE 실행 (note: [{}] → [{}], executed_date: {})", 
                                oldNote, newNote, executedDate);
                        sm.setNote(newNote);
                        sm.setExecutedDate(executedDate);  // executed_date 갱신!
                        studentManagementRepository.save(sm);
                    } else {
                        // note 동일 → 패스
                        log.info("    → note 동일하므로 패스 (executed_date 보존)");
                    }
                }
            }
        }
        
        log.info("교육지도 상세 저장 완료");
    }
}