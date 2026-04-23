package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.guardian.reqDto.SaveGuardianReqDto;
import com.taekwondo.miwool.dto.guardian.respDto.GuardianInfoDto;
import com.taekwondo.miwool.dto.guardian.respDto.GuardianInfoRespDto;
import com.taekwondo.miwool.entity.Guardian;
import com.taekwondo.miwool.entity.StudentGuardian;
import com.taekwondo.miwool.repository.GuardianRepository;
import com.taekwondo.miwool.repository.StudentGuardianRepository;
import com.taekwondo.miwool.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuardianService {
    
    private final StudentRepository studentRepository;
    private final StudentGuardianRepository studentGuardianRepository;
    private final GuardianRepository guardianRepository;
    
    /**
     * 보호자 정보 조회
     */
    public GuardianInfoRespDto getGuardianInfo(String studentCode) {
        
        log.info("보호자 정보 조회 시작: studentCode={}", studentCode);
        
        // 1. 제자 존재 여부 확인
        if (!studentRepository.existsById(studentCode)) {
            throw new IllegalArgumentException("존재하지 않는 제자입니다: " + studentCode);
        }
        
        // 2. student_guardian에서 guardianCode와 관계 조회
        List<StudentGuardian> studentGuardians = studentGuardianRepository.findByStudentCode(studentCode);
        
        // 3. guardianCode와 relationship 매핑
        Map<String, String> relationshipMap = studentGuardians.stream()
                .collect(Collectors.toMap(
                        StudentGuardian::getGuardianCode,
                        StudentGuardian::getRelationship
                ));
        
        // 4. guardianCode 목록 추출
        List<String> guardianCodes = new ArrayList<>(relationshipMap.keySet());
        
        // 5. guardian_mst에서 보호자 정보 조회
        List<Guardian> guardians = new ArrayList<>();
        if (!guardianCodes.isEmpty()) {
            guardians = guardianRepository.findAllById(guardianCodes);
        }
        
        // 6. GuardianInfoDto 리스트로 변환
        List<GuardianInfoDto> guardianInfoList = guardians.stream()
                .map(guardian -> GuardianInfoDto.builder()
                        .guardianCode(guardian.getGuardianCode())
                        .guardianName(guardian.getGuardianName())
                        .relationship(relationshipMap.get(guardian.getGuardianCode()))
                        .guardianPhone(guardian.getGuardianPhone())
                        .guardianEmergencyPhone(guardian.getGuardianEmergencyPhone())
                        .guardianBirthDate(guardian.getGuardianBirthDate())
                        .guardianJob(guardian.getGuardianJob())
                        .guardianAnniversaryDate(guardian.getGuardianAnniversaryDate())
                        .guardianRequest(guardian.getGuardianRequest())
                        .build())
                .collect(Collectors.toList());
        
        log.info("보호자 정보 조회 완료: studentCode={}, 보호자 수={}", studentCode, guardianInfoList.size());
        
        return GuardianInfoRespDto.builder()
                .guardians(guardianInfoList)
                .build();
    }
    
    /**
     * 보호자 저장 (생성/수정)
     * guardianCode가 null이면 생성, 있으면 수정
     */
    public void saveGuardian(SaveGuardianReqDto reqDto, String dojangCode) {
        
        log.info("보호자 저장 시작: guardianCode={}, studentCode={}", 
                reqDto.getGuardianCode(), reqDto.getStudentCode());
        
        // 1. 제자 존재 여부 확인
        if (!studentRepository.existsById(reqDto.getStudentCode())) {
            throw new IllegalArgumentException("존재하지 않는 제자입니다: " + reqDto.getStudentCode());
        }
        
        if (reqDto.getGuardianCode() == null || reqDto.getGuardianCode().isEmpty()) {
            // 생성
            createGuardian(reqDto, dojangCode);
        } else {
            // 수정
            updateGuardian(reqDto);
        }
        
        log.info("보호자 저장 완료: guardianCode={}", reqDto.getGuardianCode());
    }
    
    /**
     * 보호자 생성
     */
    private void createGuardian(SaveGuardianReqDto reqDto, String dojangCode) {
        
        // 1. guardianCode 자동 생성
        String guardianCode = generateGuardianCode(dojangCode);
        
        // 2. guardian_mst INSERT
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
        
        // 3. student_guardian INSERT (제자-보호자 매핑)
        StudentGuardian studentGuardian = StudentGuardian.builder()
                .studentCode(reqDto.getStudentCode())
                .guardianCode(guardianCode)
                .relationship(reqDto.getRelationship())
                .build();
        
        studentGuardianRepository.save(studentGuardian);
        
        log.info("보호자 생성 완료: guardianCode={}", guardianCode);
    }
    
    /**
     * 보호자 수정
     */
    private void updateGuardian(SaveGuardianReqDto reqDto) {
        
        String guardianCode = reqDto.getGuardianCode();
        
        // 1. 보호자 존재 여부 확인
        Guardian guardian = guardianRepository.findById(guardianCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 보호자입니다: " + guardianCode));
        
        // 2. guardian_mst UPDATE
        guardian.setGuardianName(reqDto.getGuardianName());
        guardian.setGuardianPhone(reqDto.getGuardianPhone());
        guardian.setGuardianEmergencyPhone(reqDto.getGuardianEmergencyPhone());
        guardian.setGuardianBirthDate(reqDto.getGuardianBirthDate());
        guardian.setGuardianJob(reqDto.getGuardianJob());
        guardian.setGuardianAnniversaryDate(reqDto.getGuardianAnniversaryDate());
        guardian.setGuardianRequest(reqDto.getGuardianRequest());
        
        guardianRepository.save(guardian);
        
        // 3. student_guardian의 relationship UPDATE
        StudentGuardian studentGuardian = studentGuardianRepository
                .findByStudentCodeAndGuardianCode(reqDto.getStudentCode(), guardianCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "제자-보호자 매핑 정보를 찾을 수 없습니다: studentCode=" + reqDto.getStudentCode() 
                        + ", guardianCode=" + guardianCode));
        
        studentGuardian.setRelationship(reqDto.getRelationship());
        studentGuardianRepository.save(studentGuardian);
        
        log.info("보호자 수정 완료: guardianCode={}", guardianCode);
    }
    
    /**
     * guardianCode 자동 생성
     * 형식: {dojangCode}-G{순번}
     */
    private String generateGuardianCode(String dojangCode) {
        
        // 해당 도장의 마지막 보호자 코드 조회
        String lastGuardianCode = guardianRepository.findTopByDojangCodeOrderByGuardianCodeDesc(dojangCode)
                .map(Guardian::getGuardianCode)
                .orElse(null);
        
        int nextNumber = 1;
        
        if (lastGuardianCode != null) {
            // 마지막 코드에서 순번 추출 (예: MW26001-G26001 → 26001)
            String[] parts = lastGuardianCode.split("-G");
            if (parts.length == 2) {
                nextNumber = Integer.parseInt(parts[1]) + 1;
            }
        }
        
        // 새 코드 생성 (예: MW26001-G26001)
        return String.format("%s-G%05d", dojangCode, nextNumber);
    }
    
    /**
     * 보호자 삭제
     */
    public void deleteGuardian(String studentCode, String guardianCode) {
        
        log.info("보호자 삭제 시작: studentCode={}, guardianCode={}", studentCode, guardianCode);
        
        // 1. student_guardian 삭제 (제자-보호자 매핑 삭제)
        StudentGuardian studentGuardian = studentGuardianRepository
                .findByStudentCodeAndGuardianCode(studentCode, guardianCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "제자-보호자 매핑 정보를 찾을 수 없습니다: studentCode=" + studentCode 
                        + ", guardianCode=" + guardianCode));
        
        studentGuardianRepository.delete(studentGuardian);
        
        // 2. 해당 보호자를 참조하는 다른 제자가 있는지 확인
        long count = studentGuardianRepository.countByGuardianCode(guardianCode);
        
        // 3. 다른 제자가 없으면 guardian_mst도 삭제
        if (count == 0) {
            guardianRepository.deleteById(guardianCode);
            log.info("보호자 완전 삭제: guardianCode={}", guardianCode);
        } else {
            log.info("보호자 매핑만 삭제 (다른 제자 존재): guardianCode={}, 남은 매핑 수={}", guardianCode, count);
        }
        
        log.info("보호자 삭제 완료: studentCode={}, guardianCode={}", studentCode, guardianCode);
    }
}