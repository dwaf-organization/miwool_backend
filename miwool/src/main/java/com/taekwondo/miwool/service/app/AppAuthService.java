package com.taekwondo.miwool.service.app;

import com.taekwondo.miwool.dto.app.auth.reqDto.LoginReqDto;
import com.taekwondo.miwool.dto.app.auth.respDto.LoginRespDto;
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
public class AppAuthService {
 
    private final DojangRepository dojangRepository;
    private final PasswordEncoder passwordEncoder;
 
    /**
     * 앱 로그인
     */
    @Transactional
    public LoginRespDto login(LoginReqDto reqDto) {
        log.info("앱 로그인 시도: dojangId={}", reqDto.getDojangId());
        
        // 도장 조회 (삭제되지 않은 도장만)
        Dojang dojang = dojangRepository.findActiveByDojangId(reqDto.getDojangId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(reqDto.getDojangPw(), dojang.getDojangPw())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        
        // 마지막 로그인 시간 업데이트
        dojang.setLastLoginAt(LocalDateTime.now());
        dojangRepository.save(dojang);
        
        log.info("앱 로그인 성공: dojangCode={}", dojang.getDojangCode());
        
        return LoginRespDto.builder()
                .dojangCode(dojang.getDojangCode())
                .dojangName(dojang.getDojangName())
                .build();
    }
}
 