package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.family.reqDto.SaveFamilyInfoReqDto;
import com.taekwondo.miwool.dto.family.respDto.FamilyInfoRespDto;
import com.taekwondo.miwool.entity.StudentFamily;
import com.taekwondo.miwool.entity.StudentFamilySituation;
import com.taekwondo.miwool.entity.StudentEducation;
import com.taekwondo.miwool.repository.StudentRepository;
import com.taekwondo.miwool.repository.StudentFamilyRepository;
import com.taekwondo.miwool.repository.StudentFamilySituationRepository;
import com.taekwondo.miwool.repository.StudentEducationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyService {
    
    private final StudentRepository studentRepository;
    private final StudentFamilyRepository studentFamilyRepository;
    private final StudentFamilySituationRepository studentFamilySituationRepository;
    private final StudentEducationRepository studentEducationRepository;
    
    /**
     * 가족 정보 조회
     */
    public FamilyInfoRespDto getFamilyInfo(String studentCode) {
        
        log.info("가족 정보 조회 시작: studentCode={}", studentCode);
        
        // 1. 제자 존재 여부 확인
        if (!studentRepository.existsById(studentCode)) {
            throw new IllegalArgumentException("존재하지 않는 제자입니다: " + studentCode);
        }
        
        // 2. student_family 조회
        StudentFamily studentFamily = studentFamilyRepository.findFamilyInfoByStudentCode(studentCode)
                .orElse(null);
        
        // 3. student_family_situation 조회 (가족특이사항)
        List<StudentFamilySituation> familySituationList = 
                studentFamilySituationRepository.findByStudentCode(studentCode);
        
        List<String> familySituation = familySituationList.isEmpty() ? null :
                familySituationList.stream()
                        .map(StudentFamilySituation::getFamilySituationCode)
                        .collect(Collectors.toList());
        
        String familySituationEtc = familySituationList.stream()
                .filter(e -> e.getFamilySituationCode().contains("999"))
                .findFirst()
                .map(StudentFamilySituation::getEtcValue)
                .orElse(null);
        
        // 4. student_education 조회 (부모교육가치)
        List<StudentEducation> educationList = 
                studentEducationRepository.findByStudentCode(studentCode);
        
        List<String> educationValue = educationList.isEmpty() ? null :
                educationList.stream()
                        .map(StudentEducation::getEducationValueCode)
                        .collect(Collectors.toList());
        
        String educationValueEtc = educationList.stream()
                .filter(e -> e.getEducationValueCode().contains("999"))
                .findFirst()
                .map(StudentEducation::getEtcValue)
                .orElse(null);
        
        // 5. DTO 생성
        FamilyInfoRespDto respDto = FamilyInfoRespDto.builder()
                .studentCode(studentCode)
                .familyComposition(studentFamily != null ? studentFamily.getFamilyComposition() : null)
                .familyName(studentFamily != null ? studentFamily.getFamilyName() : null)
                .familyBirth(studentFamily != null ? studentFamily.getFamilyBirth() : null)
                .siblingCount(studentFamily != null ? studentFamily.getSiblingCount() : null)
                .isAlsoStudent(studentFamily != null ? studentFamily.getIsAlsoStudent() : null)
                .primaryCaregiver(studentFamily != null ? studentFamily.getPrimaryCaregiver() : null)
                .familyNote(studentFamily != null ? studentFamily.getFamilyNote() : null)
                .familySituation(familySituation)
                .familySituationEtc(familySituationEtc)
                .educationValue(educationValue)
                .educationValueEtc(educationValueEtc)
                .build();
        
        log.info("가족 정보 조회 완료: studentCode={}", studentCode);
        
        return respDto;
    }
    
    /**
     * 가족 정보 저장 (생성/수정)
     * student_family가 없으면 생성, 있으면 수정
     */
    @Transactional
    public void saveFamilyInfo(SaveFamilyInfoReqDto reqDto) {
        
        String studentCode = reqDto.getStudentCode();
        log.info("가족 정보 저장 시작: studentCode={}", studentCode);
        
        // 1. 제자 존재 여부 확인
        if (!studentRepository.existsById(studentCode)) {
            throw new IllegalArgumentException("존재하지 않는 제자입니다: " + studentCode);
        }
        
        // 2. student_family 존재 여부 확인 후 생성/수정
        StudentFamily studentFamily = studentFamilyRepository.findFamilyInfoByStudentCode(studentCode)
                .orElse(null);
        
        if (studentFamily == null) {
            // 생성: INSERT
            studentFamily = StudentFamily.builder()
                    .studentCode(studentCode)
                    .familyComposition(reqDto.getFamilyComposition())
                    .familyName(reqDto.getFamilyName())
                    .familyBirth(reqDto.getFamilyBirth())
                    .siblingCount(reqDto.getSiblingCount())
                    .isAlsoStudent(reqDto.getIsAlsoStudent())
                    .primaryCaregiver(reqDto.getPrimaryCaregiver())
                    .familyNote(reqDto.getFamilyNote())
                    .build();
            
            log.info("가족 기본 정보 생성: studentCode={}", studentCode);
        } else {
            // 수정: UPDATE
            studentFamily.setFamilyComposition(reqDto.getFamilyComposition());
            studentFamily.setFamilyName(reqDto.getFamilyName());
            studentFamily.setFamilyBirth(reqDto.getFamilyBirth());
            studentFamily.setSiblingCount(reqDto.getSiblingCount());
            studentFamily.setIsAlsoStudent(reqDto.getIsAlsoStudent());
            studentFamily.setPrimaryCaregiver(reqDto.getPrimaryCaregiver());
            studentFamily.setFamilyNote(reqDto.getFamilyNote());
            
            log.info("가족 기본 정보 수정: studentCode={}", studentCode);
        }
        
        studentFamilyRepository.save(studentFamily);
        
        // 3. student_family_situation 처리 (DELETE → INSERT)
        studentFamilySituationRepository.deleteByStudentCode(studentCode);
        
        if (reqDto.getFamilySituation() != null && !reqDto.getFamilySituation().isEmpty()) {
            for (String code : reqDto.getFamilySituation()) {
                StudentFamilySituation entity = StudentFamilySituation.builder()
                        .studentCode(studentCode)
                        .familySituationCode(code)
                        .etcValue(code.contains("999") ? reqDto.getFamilySituationEtc() : null)
                        .build();
                studentFamilySituationRepository.save(entity);
            }
            log.info("가족특이사항 저장 완료: studentCode={}, 항목 수={}", studentCode, reqDto.getFamilySituation().size());
        } else {
            log.info("가족특이사항 삭제만 수행: studentCode={}", studentCode);
        }
        
        // 4. student_education 처리 (DELETE → INSERT)
        studentEducationRepository.deleteByStudentCode(studentCode);
        
        if (reqDto.getEducationValue() != null && !reqDto.getEducationValue().isEmpty()) {
            for (String code : reqDto.getEducationValue()) {
                StudentEducation entity = StudentEducation.builder()
                        .studentCode(studentCode)
                        .educationValueCode(code)
                        .etcValue(code.contains("999") ? reqDto.getEducationValueEtc() : null)
                        .build();
                studentEducationRepository.save(entity);
            }
            log.info("부모교육가치 저장 완료: studentCode={}, 항목 수={}", studentCode, reqDto.getEducationValue().size());
        } else {
            log.info("부모교육가치 삭제만 수행: studentCode={}", studentCode);
        }
        
        log.info("가족 정보 저장 완료: studentCode={}", studentCode);
    }
    
}