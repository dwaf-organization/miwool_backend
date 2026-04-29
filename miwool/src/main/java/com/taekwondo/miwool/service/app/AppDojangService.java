package com.taekwondo.miwool.service.app;

import com.taekwondo.miwool.dto.app.mypage.respDto.DojangInfoRespDto;
import com.taekwondo.miwool.entity.Dojang;
import com.taekwondo.miwool.repository.DojangRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppDojangService {

    private final DojangRepository dojangRepository;

    /**
     * 앱 도장정보 조회
     */
    @Transactional(readOnly = true)
    public DojangInfoRespDto getDojangInfo(String dojangCode) {
        log.info("앱 도장정보 조회: dojangCode={}", dojangCode);
        
        // 도장 정보 조회
        Dojang dojang = dojangRepository.findById(dojangCode)
                .orElseThrow(() -> new IllegalArgumentException("도장 정보를 찾을 수 없습니다."));
        
        // DTO 변환 (비밀번호, 삭제 관련 필드 제외)
        DojangInfoRespDto respDto = DojangInfoRespDto.builder()
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
                .note(dojang.getNote())
                .approvalYn(dojang.getApprovalYn())
                .lastLoginAt(dojang.getLastLoginAt())
                .createdAt(dojang.getCreatedAt())
                .updatedAt(dojang.getUpdatedAt())
                .build();
        
        log.info("앱 도장정보 조회 완료: dojangName={}", dojang.getDojangName());
        
        return respDto;
    }
}