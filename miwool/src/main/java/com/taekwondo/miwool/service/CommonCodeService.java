package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.common.respDto.CommonCodeRespDto;
import com.taekwondo.miwool.dto.common.respDto.StudentSelectionRespDto;
import com.taekwondo.miwool.dto.common.respDto.TraitOptionDto;
import com.taekwondo.miwool.entity.CommonCode;
import com.taekwondo.miwool.entity.Student;
import com.taekwondo.miwool.repository.CommonCodeRepository;
import com.taekwondo.miwool.repository.StudentRepository;
import com.taekwondo.miwool.util.AgeUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommonCodeService {
    
    private final CommonCodeRepository commonCodeRepository;
    private final StudentRepository studentRepository;
    
    /**
     * 그룹코드별 공통코드 조회
     */
    public List<CommonCodeRespDto> getCodesByGroupCode(String groupCode) {
        
        List<CommonCode> codes = commonCodeRepository.findByGroupCodeAndUseYnOrderByCodeOrderAsc(groupCode, "Y");
        
        log.info("공통코드 조회: {} - {}건", groupCode, codes.size());
        
        return codes.stream()
                .map(code -> CommonCodeRespDto.builder()
                        .commonCode(code.getCommonCode())
                        .codeName(code.getCodeName())
                        .codeOrder(code.getCodeOrder())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 특성 항목 조회 (공통코드에서 가져오기)
     */
    public Map<String, List<TraitOptionDto>> getCharacterTraitOptions() {
        
        log.info("특성 항목 조회 시작");
        
        Map<String, List<TraitOptionDto>> result = new HashMap<>();
        
        // 1. 성향_기본 (PERS_BASIC)
        List<CommonCode> persBasicList = commonCodeRepository.findByGroupCodeAndUseYnOrderByCodeOrderAsc("PERS_BASIC", "Y");
        result.put("personalityBasic", persBasicList.stream()
                .map(code -> TraitOptionDto.builder()
                        .code(code.getCommonCode())
                        .name(code.getCodeName())
                        .build())
                .collect(Collectors.toList()));
        
        // 2. 성향_정서 (PERS_EMOT)
        List<CommonCode> persEmotList = commonCodeRepository.findByGroupCodeAndUseYnOrderByCodeOrderAsc("PERS_EMOT", "Y");
        result.put("emotion", persEmotList.stream()
                .map(code -> TraitOptionDto.builder()
                        .code(code.getCommonCode())
                        .name(code.getCodeName())
                        .build())
                .collect(Collectors.toList()));
        
        // 3. 성향_사회성 (PERS_SOC)
        List<CommonCode> persSocList = commonCodeRepository.findByGroupCodeAndUseYnOrderByCodeOrderAsc("PERS_SOC", "Y");
        result.put("sociability", persSocList.stream()
                .map(code -> TraitOptionDto.builder()
                        .code(code.getCommonCode())
                        .name(code.getCodeName())
                        .build())
                .collect(Collectors.toList()));
        
        // 4. 성향_수업반응 (PERS_LESSON)
        List<CommonCode> persLessonList = commonCodeRepository.findByGroupCodeAndUseYnOrderByCodeOrderAsc("PERS_LESSON", "Y");
        result.put("lessonResponse", persLessonList.stream()
                .map(code -> TraitOptionDto.builder()
                        .code(code.getCommonCode())
                        .name(code.getCodeName())
                        .build())
                .collect(Collectors.toList()));
        
        // 5. 건강특성 (HEALTH_FEAT)
        List<CommonCode> healthFeatList = commonCodeRepository.findByGroupCodeAndUseYnOrderByCodeOrderAsc("HEALTH_FEAT", "Y");
        result.put("healthTrait", healthFeatList.stream()
                .map(code -> TraitOptionDto.builder()
                        .code(code.getCommonCode())
                        .name(code.getCodeName())
                        .build())
                .collect(Collectors.toList()));
        
        // 6. 체형특성 (BODY_FEAT)
        List<CommonCode> bodyFeatList = commonCodeRepository.findByGroupCodeAndUseYnOrderByCodeOrderAsc("BODY_FEAT", "Y");
        result.put("bodyTrait", bodyFeatList.stream()
                .map(code -> TraitOptionDto.builder()
                        .code(code.getCommonCode())
                        .name(code.getCodeName())
                        .build())
                .collect(Collectors.toList()));
        
        // 7. 신체민감사항 (BODY_SENS)
        List<CommonCode> bodySensList = commonCodeRepository.findByGroupCodeAndUseYnOrderByCodeOrderAsc("BODY_SENS", "Y");
        result.put("bodySensitive", bodySensList.stream()
                .map(code -> TraitOptionDto.builder()
                        .code(code.getCommonCode())
                        .name(code.getCodeName())
                        .build())
                .collect(Collectors.toList()));
        
        // 8. 변화필요부분 (CHANGE_NEED)
        List<CommonCode> changeNeedList = commonCodeRepository.findByGroupCodeAndUseYnOrderByCodeOrderAsc("CHANGE_NEED", "Y");
        result.put("changeNeed", changeNeedList.stream()
                .map(code -> TraitOptionDto.builder()
                        .code(code.getCommonCode())
                        .name(code.getCodeName())
                        .build())
                .collect(Collectors.toList()));
        
        // 9. 강점 (STRENGTH)
        List<CommonCode> strengthList = commonCodeRepository.findByGroupCodeAndUseYnOrderByCodeOrderAsc("STRENGTH", "Y");
        result.put("strength", strengthList.stream()
                .map(code -> TraitOptionDto.builder()
                        .code(code.getCommonCode())
                        .name(code.getCodeName())
                        .build())
                .collect(Collectors.toList()));
        
        log.info("특성 항목 조회 완료");
        
        return result;
    }
    
    /**
     * 제자 선택 팝업 조회
     * 조건: 제자명(코드/명 부분조회), 급수, 성별, 학년
     */
    public List<StudentSelectionRespDto> getStudentSelection(
            String studentSearch,
            String beltCode,
            Integer genderCode,
            String grade,
            String dojangCode) {
        
        log.info("제자 선택 팝업 조회 시작: studentSearch={}, beltCode={}, genderCode={}, grade={}", 
                studentSearch, beltCode, genderCode, grade);
        
        // 정렬 설정 (제자명 오름차순)
        Sort sort = Sort.by(Sort.Direction.ASC, "student_name");
        
        // Repository에서 Native Query로 조회
        List<Student> students = studentRepository.findStudentsForSelection(
                studentSearch,
                beltCode,
                genderCode,
                grade,
                dojangCode,
                sort
        );
        
        // DTO 변환
        List<StudentSelectionRespDto> result = students.stream()
                .map((Student student) -> {
                    // 한국나이 계산
                    int age = AgeUtil.calculateKoreanAge(student.getBirthDate());
                    
                    return StudentSelectionRespDto.builder()
                            .studentCode(student.getStudentCode())
                            .genderCode(student.getGenderCode())
                            .studentName(student.getStudentName())
                            .age(age)
                            .grade(student.getGrade())
                            .beltCode(student.getBeltCode())
                            .build();
                })
                .collect(Collectors.toList());
        
        log.info("제자 선택 팝업 조회 완료: 조회 건수={}", result.size());
        
        return result;
    }
    
}