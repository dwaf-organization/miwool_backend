package com.taekwondo.miwool.service.admin;

import com.taekwondo.miwool.dto.admin.dojang.reqDto.DojangUpdateReqDto;
import com.taekwondo.miwool.dto.admin.dojang.reqDto.VipPackageBulkUpdateReqDto;
import com.taekwondo.miwool.dto.admin.dojang.reqDto.VipPackageUpdateReqDto;
import com.taekwondo.miwool.dto.admin.dojang.respDto.DojangDetailRespDto;
import com.taekwondo.miwool.dto.admin.dojang.respDto.DojangItemDto;
import com.taekwondo.miwool.dto.admin.dojang.respDto.VipPackageBulkUpdateRespDto;
import com.taekwondo.miwool.entity.Dojang;
import com.taekwondo.miwool.repository.DojangRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminDojangService {

    private final DojangRepository dojangRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 관리자 도장 목록 조회
     */
    @Transactional(readOnly = true)
    public List<DojangItemDto> getDojangs(
            String dojangSearch,
            String dojangStatus,
            String approvalYn,
            String vipPackage) {
        
        log.info("관리자 도장 목록 조회: dojangSearch={}, dojangStatus={}, approvalYn={}, vipPackage={}", 
                dojangSearch, dojangStatus, approvalYn, vipPackage);
        
        // 도장 목록 조회 (is_deleted=0만)
        List<Object[]> results = dojangRepository.findDojangsForAdmin(
                dojangSearch, dojangStatus, approvalYn, vipPackage);
        
        // Object[] → DTO 변환
        List<DojangItemDto> dojangs = results.stream()
                .map(row -> DojangItemDto.builder()
                        .dojangCode((String) row[0])
                        .dojangName((String) row[1])
                        .masterName((String) row[2])
                        .masterPhone((String) row[3])
                        .dojangTel((String) row[4])
                        .dojangAddress((String) row[5])
                        .dojangStatus((String) row[6])
                        .approvalYn(((Number) row[7]).intValue())
                        .vipPackage((String) row[8])
                        .build())
                .collect(Collectors.toList());
        
        log.info("관리자 도장 목록 조회 완료: 총 {}건", dojangs.size());
        
        return dojangs;
    }

    /**
     * 도장 상세 정보 조회
     */
    @Transactional(readOnly = true)
    public DojangDetailRespDto getDojangDetail(String dojangCode) {
        log.info("도장 상세 정보 조회: dojangCode={}", dojangCode);
        
        // 도장 조회
        Dojang dojang = dojangRepository.findById(dojangCode)
                .orElseThrow(() -> new IllegalArgumentException("도장을 찾을 수 없습니다."));
        
        // Entity → DTO 변환 (비밀번호 제외)
        DojangDetailRespDto respDto = DojangDetailRespDto.builder()
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
                .vipPackage(dojang.getVipPackage())
                .isDeleted(dojang.getIsDeleted())
                .deletedAt(dojang.getDeletedAt())
                .note(dojang.getNote())
                .approvalYn(dojang.getApprovalYn())
                .lastLoginAt(dojang.getLastLoginAt())
                .createdAt(dojang.getCreatedAt())
                .updatedAt(dojang.getUpdatedAt())
                .build();
        
        log.info("도장 상세 정보 조회 완료: {}", dojang.getDojangName());
        
        return respDto;
    }

    /**
     * 도장 정보 수정
     */
    @Transactional
    public void updateDojang(DojangUpdateReqDto reqDto) {
        log.info("도장 정보 수정: dojangCode={}", reqDto.getDojangCode());
        
        // 도장 조회
        Dojang dojang = dojangRepository.findById(reqDto.getDojangCode())
                .orElseThrow(() -> new IllegalArgumentException("도장을 찾을 수 없습니다."));
        
        // 수정 가능한 필드만 업데이트
        dojang.setDojangName(reqDto.getDojangName());
        dojang.setMasterName(reqDto.getMasterName());
        dojang.setMasterBirth(reqDto.getMasterBirth());
        dojang.setMasterPhone(reqDto.getMasterPhone());
        dojang.setDojangEmail(reqDto.getDojangEmail());
        dojang.setDojangBizNum(reqDto.getDojangBizNum());
        dojang.setDojangZipcode(reqDto.getDojangZipcode());
        dojang.setDojangAdd(reqDto.getDojangAdd());
        dojang.setDojangAdd2(reqDto.getDojangAdd2());
        dojang.setDojangStatus(reqDto.getDojangStatus());
        dojang.setSelectedSchool(reqDto.getSelectedSchool());
        dojang.setSelectedSchool2(reqDto.getSelectedSchool2());
        dojang.setVipPackage(reqDto.getVipPackage());
        
        // 폐관이어도 is_deleted, deleted_at는 변경하지 않음
        
        dojangRepository.save(dojang);
        
        log.info("도장 정보 수정 완료: dojangCode={}, dojangName={}", reqDto.getDojangCode(), dojang.getDojangName());
    }

    /**
     * 비밀번호 초기화
     */
    @Transactional
    public void resetPassword(String dojangCode) {
        log.info("비밀번호 초기화: dojangCode={}", dojangCode);
        
        // 도장 조회
        Dojang dojang = dojangRepository.findById(dojangCode)
                .orElseThrow(() -> new IllegalArgumentException("도장을 찾을 수 없습니다."));
        
        // 비밀번호 초기화 (mw12345)
        String tempPassword = "mw12345";
        String encodedPassword = passwordEncoder.encode(tempPassword);
        
        dojang.setDojangPw(encodedPassword);
        dojangRepository.save(dojang);
        
        log.info("비밀번호 초기화 완료: dojangCode={}, dojangName={}", dojangCode, dojang.getDojangName());
    }

    /**
     * 도장 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteDojang(String dojangCode) {
        log.info("도장 삭제 처리: dojangCode={}", dojangCode);
        
        // 도장 조회
        Dojang dojang = dojangRepository.findById(dojangCode)
                .orElseThrow(() -> new IllegalArgumentException("도장을 찾을 수 없습니다."));
        
        // 소프트 삭제
        dojang.setIsDeleted(1);
        dojang.setDeletedAt(java.time.LocalDateTime.now());
        dojangRepository.save(dojang);
        
        log.info("도장 삭제 완료: dojangCode={}, dojangName={}", dojangCode, dojang.getDojangName());
    }

    /**
     * 도장 승인 처리
     */
    @Transactional
    public void approveDojang(String dojangCode) {
        log.info("도장 승인 처리: dojangCode={}", dojangCode);
        
        // 도장 조회
        Dojang dojang = dojangRepository.findById(dojangCode)
                .orElseThrow(() -> new IllegalArgumentException("도장을 찾을 수 없습니다."));
        
        // 승인 처리
        dojang.setApprovalYn(1);
        dojangRepository.save(dojang);
        
        log.info("도장 승인 처리 완료: dojangCode={}, dojangName={}", dojangCode, dojang.getDojangName());
    }

    /**
     * VIP 패키지 일괄 저장
     */
    @Transactional
    public VipPackageBulkUpdateRespDto updateVipPackages(VipPackageBulkUpdateReqDto reqDto) {
        log.info("VIP 패키지 일괄 저장: {}건", reqDto.getUpdates().size());
        
        int updatedCount = 0;
        
        for (VipPackageUpdateReqDto update : reqDto.getUpdates()) {
            try {
                // 도장 조회
                Dojang dojang = dojangRepository.findById(update.getDojangCode())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "도장을 찾을 수 없습니다: " + update.getDojangCode()));
                
                // VIP 패키지 업데이트
                dojang.setVipPackage(update.getVipPackage());
                dojangRepository.save(dojang);
                
                updatedCount++;
                
                log.info("VIP 패키지 업데이트 완료: dojangCode={}, vipPackage={}", 
                        update.getDojangCode(), update.getVipPackage());
                
            } catch (Exception e) {
                log.error("VIP 패키지 업데이트 실패: dojangCode={}", update.getDojangCode(), e);
                throw new RuntimeException("VIP 패키지 업데이트 중 오류가 발생했습니다: " + update.getDojangCode());
            }
        }
        
        log.info("VIP 패키지 일괄 저장 완료: {}건", updatedCount);
        
        return VipPackageBulkUpdateRespDto.builder()
                .updatedCount(updatedCount)
                .build();
    }
}