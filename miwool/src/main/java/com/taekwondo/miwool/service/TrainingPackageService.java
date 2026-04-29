package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.training.reqDto.CreateTrainingPackageReqDto;
import com.taekwondo.miwool.dto.training.reqDto.UpdateTrainingPackageReqDto;
import com.taekwondo.miwool.dto.training.respDto.PackageOptionRespDto;
import com.taekwondo.miwool.dto.training.respDto.TrainingPackageRespDto;
import com.taekwondo.miwool.entity.TrainingMst;
import com.taekwondo.miwool.repository.TrainingMstRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingPackageService {
    
    private final TrainingMstRepository trainingMstRepository;
    
    /**
     * 패키지 생성
     */
    @Transactional
    public void createPackage(CreateTrainingPackageReqDto reqDto, String dojangCode) {
        log.info("패키지 생성: dojangCode={}, packageName={}", dojangCode, reqDto.getPackageName());
        
        // packageCode 자동 생성 (PKG001, PKG002, ...)
        String packageCode = generatePackageCode(dojangCode);
        
        TrainingMst trainingMst = TrainingMst.builder()
                .packageCode(packageCode)
                .dojangCode(dojangCode)
                .packageName(reqDto.getPackageName())
                .weeklyCount(reqDto.getWeeklyCount())
                .basePrice(reqDto.getBasePrice())
                .description(reqDto.getDescription())
                .useYn("Y")  // 기본값 Y
                .build();
        
        trainingMstRepository.save(trainingMst);
        
        log.info("패키지 생성 완료: packageCode={}", packageCode);
    }
    
    /**
     * 패키지 목록 조회 (최신순)
     */
    public List<TrainingPackageRespDto> getPackageList(String dojangCode) {
        log.info("패키지 목록 조회: dojangCode={}", dojangCode);
        
        List<TrainingMst> packages = trainingMstRepository
                .findByDojangCodeOrderByCreatedAtDesc(dojangCode);
        
        return packages.stream()
                .map(pkg -> TrainingPackageRespDto.builder()
                        .packageCode(pkg.getPackageCode())
                        .packageName(pkg.getPackageName())
                        .weeklyCount(pkg.getWeeklyCount())
                        .basePrice(pkg.getBasePrice())
                        .description(pkg.getDescription())
                        .useYn(pkg.getUseYn())
                        .createdAt(pkg.getCreatedAt())
                        .updatedAt(pkg.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 패키지 선택용 목록 조회
     * "패키지명 / 주n회 / 교육비"
     */
    public List<PackageOptionRespDto> getPackageOptions(String dojangCode) {
        log.info("패키지 선택용 목록 조회: dojangCode={}", dojangCode);
        
        List<TrainingMst> packages = trainingMstRepository
                .findByDojangCodeOrderByPackageCodeAsc(dojangCode);
        
        return packages.stream()
                .map(pkg -> {
                    String displayText = String.format("%s / 주%d회 / %,d원",
                            pkg.getPackageName(),
                            pkg.getWeeklyCount(),
                            pkg.getBasePrice());
                    
                    return PackageOptionRespDto.builder()
                            .packageCode(pkg.getPackageCode())
                            .displayText(displayText)
                            .packageName(pkg.getPackageName())
                            .weeklyCount(pkg.getWeeklyCount())
                            .basePrice(pkg.getBasePrice())
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 패키지 상세 조회
     */
    public TrainingPackageRespDto getPackageDetail(String packageCode, String dojangCode) {
        log.info("패키지 상세 조회: packageCode={}", packageCode);
        
        TrainingMst trainingMst = trainingMstRepository.findById(packageCode)
                .orElseThrow(() -> new IllegalArgumentException("패키지를 찾을 수 없습니다: " + packageCode));
        
        // 본인 도장 패키지인지 확인
        if (!trainingMst.getDojangCode().equals(dojangCode)) {
            throw new IllegalArgumentException("조회 권한이 없습니다.");
        }
        
        return TrainingPackageRespDto.builder()
                .packageCode(trainingMst.getPackageCode())
                .packageName(trainingMst.getPackageName())
                .weeklyCount(trainingMst.getWeeklyCount())
                .basePrice(trainingMst.getBasePrice())
                .description(trainingMst.getDescription())
                .useYn(trainingMst.getUseYn())
                .createdAt(trainingMst.getCreatedAt())
                .updatedAt(trainingMst.getUpdatedAt())
                .build();
    }
    
    /**
     * 패키지 수정
     */
    @Transactional
    public void updatePackage(UpdateTrainingPackageReqDto reqDto, String dojangCode) {
        log.info("패키지 수정: packageCode={}", reqDto.getPackageCode());
        
        TrainingMst trainingMst = trainingMstRepository.findById(reqDto.getPackageCode())
                .orElseThrow(() -> new IllegalArgumentException("패키지를 찾을 수 없습니다: " + reqDto.getPackageCode()));
        
        // 본인 도장 패키지인지 확인
        if (!trainingMst.getDojangCode().equals(dojangCode)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        
        // 수정
        trainingMst.setPackageName(reqDto.getPackageName());
        trainingMst.setWeeklyCount(reqDto.getWeeklyCount());
        trainingMst.setBasePrice(reqDto.getBasePrice());
        trainingMst.setDescription(reqDto.getDescription());
        trainingMst.setUseYn(reqDto.getUseYn());
        
        trainingMstRepository.save(trainingMst);
        
        log.info("패키지 수정 완료: packageCode={}", reqDto.getPackageCode());
    }
    
    /**
     * 패키지 삭제 (하드 딜리트)
     */
    @Transactional
    public void deletePackage(String packageCode, String dojangCode) {
        log.info("패키지 삭제: packageCode={}", packageCode);
        
        TrainingMst trainingMst = trainingMstRepository.findById(packageCode)
                .orElseThrow(() -> new IllegalArgumentException("패키지를 찾을 수 없습니다: " + packageCode));
        
        // 본인 도장 패키지인지 확인
        if (!trainingMst.getDojangCode().equals(dojangCode)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        
        trainingMstRepository.delete(trainingMst);
        
        log.info("패키지 삭제 완료: packageCode={}", packageCode);
    }
    
    /**
     * packageCode 자동 생성
     * 형식: {dojangCode}-PKG001
     */
    private String generatePackageCode(String dojangCode) {
        String prefix = dojangCode + "-PKG";
        
        return trainingMstRepository.findFirstByPackageCodeStartingWithOrderByPackageCodeDesc(prefix)
                .map(entity -> {
                    String lastCode = entity.getPackageCode();
                    int nextSeq = Integer.parseInt(lastCode.substring(lastCode.length() - 3)) + 1;
                    return prefix + String.format("%03d", nextSeq);
                })
                .orElse(prefix + "001");
    }
}