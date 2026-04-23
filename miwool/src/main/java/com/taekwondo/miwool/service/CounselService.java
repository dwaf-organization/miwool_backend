package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.counsel.reqDto.CreateCounselReqDto;
import com.taekwondo.miwool.dto.counsel.reqDto.UpdateCounselReqDto;
import com.taekwondo.miwool.dto.counsel.respDto.CounselDetailRespDto;
import com.taekwondo.miwool.dto.counsel.respDto.CounselListItemDto;
import com.taekwondo.miwool.dto.counsel.respDto.CounselListRespDto;
import com.taekwondo.miwool.entity.Student;
import com.taekwondo.miwool.entity.StudentCounsel;
import com.taekwondo.miwool.repository.StudentRepository;
import com.taekwondo.miwool.repository.StudentCounselRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CounselService {
    
    private final StudentRepository studentRepository;
    private final StudentCounselRepository studentCounselRepository;
    
    /**
     * 상담 목록 조회 (페이징, 최신순)
     */
    public CounselListRespDto getCounselList(String studentCode, int page, int size) {
        
        log.info("상담 목록 조회 시작: studentCode={}, page={}, size={}", studentCode, page, size);
        
        // 1. 제자 존재 여부 확인
        if (!studentRepository.existsById(studentCode)) {
            throw new IllegalArgumentException("존재하지 않는 제자입니다: " + studentCode);
        }
        
        // 2. 페이징 설정 (최신순: counsel_date DESC)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "counselDate"));
        
        // 3. 상담 목록 조회
        Page<StudentCounsel> counselPage = studentCounselRepository.findByStudentCode(studentCode, pageable);
        
        // 4. DTO 변환
        List<CounselListItemDto> counsels = counselPage.getContent().stream()
                .map(counsel -> CounselListItemDto.builder()
                        .counselCode(counsel.getCounselCode())
                        .counselType(counsel.getCounselType())
                        .counselDate(counsel.getCounselDate())
                        .build())
                .collect(Collectors.toList());
        
        log.info("상담 목록 조회 완료: studentCode={}, 조회 건수={}", studentCode, counsels.size());
        
        return CounselListRespDto.builder()
                .counsels(counsels)
                .totalPages(counselPage.getTotalPages())
                .totalElements(counselPage.getTotalElements())
                .currentPage(page)
                .size(size)
                .build();
    }
    
    /**
     * 상담 상세 조회
     */
    public CounselDetailRespDto getCounselDetail(String counselCode) {
        
        log.info("상담 상세 조회 시작: counselCode={}", counselCode);
        
        // 1. 상담 조회
        StudentCounsel counsel = studentCounselRepository.findById(counselCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담입니다: " + counselCode));
        
        // 2. 제자 정보 조회 (제자명 가져오기)
        Student student = studentRepository.findById(counsel.getStudentCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 제자입니다: " + counsel.getStudentCode()));
        
        log.info("상담 상세 조회 완료: counselCode={}", counselCode);
        
        return CounselDetailRespDto.builder()
                .counselCode(counsel.getCounselCode())
                .counselDate(counsel.getCounselDate())
                .studentName(student.getStudentName())
                .counselType(counsel.getCounselType())
                .counselContent(counsel.getCounselContent())
                .followUp(counsel.getFollowUp())
                .build();
    }
    
    /**
     * 상담 생성
     */
    @Transactional
    public void createCounsel(CreateCounselReqDto reqDto, String dojangCode) {
        
        log.info("상담 생성 시작: studentCode={}", reqDto.getStudentCode());
        
        // 1. 제자 존재 여부 확인
        if (!studentRepository.existsById(reqDto.getStudentCode())) {
            throw new IllegalArgumentException("존재하지 않는 제자입니다: " + reqDto.getStudentCode());
        }
        
        // 2. counselCode 자동 생성
        String counselCode = generateCounselCode(dojangCode);
        
        // 3. 상담 생성
        StudentCounsel counsel = StudentCounsel.builder()
                .counselCode(counselCode)
                .studentCode(reqDto.getStudentCode())
                .counselDate(reqDto.getCounselDate())
                .counselType(reqDto.getCounselType())
                .counselContent(reqDto.getCounselContent())
                .followUp(reqDto.getFollowUp())
                .build();
        
        studentCounselRepository.save(counsel);
        
        log.info("상담 생성 완료: counselCode={}", counsel.getCounselCode());
    }
    
    /**
     * counselCode 자동 생성 (도장코드-SCYYnnn)
     * 예: MW26001-SC26001
     */
    private String generateCounselCode(String dojangCode) {
        String year = String.valueOf(LocalDate.now().getYear()).substring(2);
        String prefix = dojangCode + "-SC" + year;

        return studentCounselRepository.findFirstByCounselCodeStartingWithOrderByCounselCodeDesc(prefix)
                .map(counsel -> {
                    String lastCode = counsel.getCounselCode();
                    int nextSeq = Integer.parseInt(lastCode.substring(lastCode.length() - 3)) + 1;
                    return prefix + String.format("%03d", nextSeq);
                })
                .orElse(prefix + "001");
    }
    
    /**
     * 상담 수정
     */
    @Transactional
    public void updateCounsel(UpdateCounselReqDto reqDto) {
        
        log.info("상담 수정 시작: counselCode={}", reqDto.getCounselCode());
        
        // 1. 상담 존재 여부 확인
        StudentCounsel counsel = studentCounselRepository.findById(reqDto.getCounselCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담입니다: " + reqDto.getCounselCode()));
        
        // 2. 제자코드 일치 확인
        if (!counsel.getStudentCode().equals(reqDto.getStudentCode())) {
            throw new IllegalArgumentException("상담의 제자코드가 일치하지 않습니다");
        }
        
        // 3. 상담 수정
        counsel.setCounselDate(reqDto.getCounselDate());
        counsel.setCounselType(reqDto.getCounselType());
        counsel.setCounselContent(reqDto.getCounselContent());
        counsel.setFollowUp(reqDto.getFollowUp());
        
        studentCounselRepository.save(counsel);
        
        log.info("상담 수정 완료: counselCode={}", reqDto.getCounselCode());
    }
    
    /**
     * 상담 삭제
     */
    @Transactional
    public void deleteCounsel(String counselCode, String studentCode) {
        
        log.info("상담 삭제 시작: counselCode={}, studentCode={}", counselCode, studentCode);
        
        // 1. 상담 존재 여부 확인
        StudentCounsel counsel = studentCounselRepository.findById(counselCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담입니다: " + counselCode));
        
        // 2. 제자코드 일치 확인
        if (!counsel.getStudentCode().equals(studentCode)) {
            throw new IllegalArgumentException("상담의 제자코드가 일치하지 않습니다");
        }
        
        // 3. 상담 삭제
        studentCounselRepository.delete(counsel);
        
        log.info("상담 삭제 완료: counselCode={}", counselCode);
    }
}