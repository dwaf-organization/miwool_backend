package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.mypage.reqDto.UpdateDojangInfoReqDto;
import com.taekwondo.miwool.dto.mypage.respDto.DojangInfoRespDto;
import com.taekwondo.miwool.entity.Dojang;
import com.taekwondo.miwool.repository.DojangRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {
    
    private final DojangRepository dojangRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 도장 정보 조회
     */
    public DojangInfoRespDto getDojangInfo(String dojangCode) {
        log.info("도장 정보 조회: dojangCode={}", dojangCode);
        
        Dojang dojang = dojangRepository.findById(dojangCode)
                .orElseThrow(() -> new IllegalArgumentException("도장을 찾을 수 없습니다: " + dojangCode));
        
        return DojangInfoRespDto.builder()
                .dojangCode(dojang.getDojangCode())
                .dojangId(dojang.getDojangId())
                .dojangName(dojang.getDojangName())
                .masterName(dojang.getMasterName())
                .masterBirth(dojang.getMasterBirth())
                .masterPhone(dojang.getMasterPhone())
                .dojangTel(dojang.getDojangTel())
                .dojangEmail(dojang.getDojangEmail())
                .dojangBizNum(dojang.getDojangBizNum())
                .dojangZipcode(dojang.getDojangZipcode())
                .dojangAdd(dojang.getDojangAdd())
                .dojangAdd2(dojang.getDojangAdd2())
                .dojangStatus(dojang.getDojangStatus())
                .selectedSchool(dojang.getSelectedSchool())
                .selectedSchool2(dojang.getSelectedSchool2())
                .isDeleted(dojang.getIsDeleted())
                .deletedAt(dojang.getDeletedAt())
                .note(dojang.getNote())
                .approvalYn(dojang.getApprovalYn())
                .lastLoginAt(dojang.getLastLoginAt())
                .createdAt(dojang.getCreatedAt())
                .updatedAt(dojang.getUpdatedAt())
                .build();
    }
    
    /**
     * 도장 정보 수정
     * 비밀번호는 값이 있을 때만 수정
     */
    @Transactional
    public void updateDojangInfo(String dojangCode, UpdateDojangInfoReqDto reqDto) {
        log.info("도장 정보 수정: dojangCode={}", dojangCode);
        
        Dojang dojang = dojangRepository.findById(dojangCode)
                .orElseThrow(() -> new IllegalArgumentException("도장을 찾을 수 없습니다: " + dojangCode));
        
        // 비밀번호 수정 (값이 있을 때만)
        if (reqDto.getDojangPw() != null && !reqDto.getDojangPw().trim().isEmpty()) {
            String encodedPw = passwordEncoder.encode(reqDto.getDojangPw());
            dojang.setDojangPw(encodedPw);
            log.info("비밀번호 수정됨");
        }
        
        // 나머지 정보 수정
        dojang.setDojangName(reqDto.getDojangName());
        dojang.setMasterName(reqDto.getMasterName());
        dojang.setMasterBirth(reqDto.getMasterBirth());
        dojang.setMasterPhone(reqDto.getMasterPhone());
        dojang.setDojangTel(reqDto.getDojangTel());
        dojang.setDojangEmail(reqDto.getDojangEmail());
        dojang.setDojangBizNum(reqDto.getDojangBizNum());
        dojang.setDojangZipcode(reqDto.getDojangZipcode());
        dojang.setDojangAdd(reqDto.getDojangAdd());
        dojang.setDojangAdd2(reqDto.getDojangAdd2());
        dojang.setSelectedSchool(reqDto.getSelectedSchool());
        dojang.setSelectedSchool2(reqDto.getSelectedSchool2());
        dojang.setNote(reqDto.getNote());
        
        dojangRepository.save(dojang);
        
        log.info("도장 정보 수정 완료");
    }
    
    /**
     * 도장 탈퇴
     * dojang_status='퇴관', is_deleted=1, deleted_at=현재시간
     */
    @Transactional
    public void withdrawDojang(String dojangCode) {
        log.info("도장 탈퇴: dojangCode={}", dojangCode);
        
        Dojang dojang = dojangRepository.findById(dojangCode)
                .orElseThrow(() -> new IllegalArgumentException("도장을 찾을 수 없습니다: " + dojangCode));
        
        // 탈퇴 처리
        dojang.setDojangStatus("퇴관");
        dojang.setIsDeleted(1);
        dojang.setDeletedAt(LocalDateTime.now());
        
        dojangRepository.save(dojang);
        
        log.info("도장 탈퇴 완료: dojangCode={}, deleted_at={}", dojangCode, dojang.getDeletedAt());
    }
}