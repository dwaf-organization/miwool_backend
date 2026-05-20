package com.taekwondo.miwool.service.app;

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
public class AppWithdrawService {

    private final DojangRepository dojangRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원탈퇴 처리
     */
    @Transactional
    public void withdrawAccount(String dojangId, String dojangPw) {
        log.info("회원탈퇴 시도: dojangId={}", dojangId);
        
        // 1. 아이디로 계정 조회
        Dojang dojang = dojangRepository.findByDojangId(dojangId)
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
        
        // 2. 이미 탈퇴된 계정인지 확인
        if (dojang.getIsDeleted() == 1) {
            throw new IllegalStateException("이미 탈퇴된 계정입니다.");
        }
        
        // 3. 비밀번호 검증
        if (!passwordEncoder.matches(dojangPw, dojang.getDojangPw())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        
        // 4. 소프트 삭제 처리
        dojang.setIsDeleted(1);
        dojang.setDeletedAt(LocalDateTime.now());
        dojangRepository.save(dojang);
        
        log.info("회원탈퇴 완료: dojangId={}, dojangCode={}", dojangId, dojang.getDojangCode());
    }
}