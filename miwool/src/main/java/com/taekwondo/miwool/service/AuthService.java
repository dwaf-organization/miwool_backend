package com.taekwondo.miwool.service;

import com.taekwondo.miwool.dto.auth.reqDto.FindIdReqDto;
import com.taekwondo.miwool.dto.auth.reqDto.LoginReqDto;
import com.taekwondo.miwool.dto.auth.reqDto.RefreshTokenReqDto;
import com.taekwondo.miwool.dto.auth.reqDto.RegisterReqDto;
import com.taekwondo.miwool.dto.auth.reqDto.ResetPwReqDto;
import com.taekwondo.miwool.dto.auth.respDto.CheckIdRespDto;
import com.taekwondo.miwool.dto.auth.respDto.FindIdRespDto;
import com.taekwondo.miwool.dto.auth.respDto.LoginRespDto;
import com.taekwondo.miwool.dto.auth.respDto.RefreshTokenRespDto;
import com.taekwondo.miwool.dto.auth.respDto.RegisterRespDto;
import com.taekwondo.miwool.dto.auth.respDto.ResetPwRespDto;
import com.taekwondo.miwool.entity.Dojang;
import com.taekwondo.miwool.entity.SignupAlarm;
import com.taekwondo.miwool.repository.DojangRepository;
import com.taekwondo.miwool.repository.SignupAlarmRepository;
import com.taekwondo.miwool.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final DojangRepository dojangRepository;
    private final SignupAlarmRepository signupAlarmRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    /**
     * 회원가입
     */
    @Transactional
    public RegisterRespDto register(RegisterReqDto reqDto) {
        
        // 1. 아이디 중복 체크
        if (dojangRepository.existsByDojangId(reqDto.getDojangId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        
        // 2. 도장 코드 생성 (MW-도장명)
        String dojangCode = generateDojangCode();
        
        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(reqDto.getDojangPw());
        
        // 4. Dojang 엔티티 생성
        Dojang dojang = Dojang.builder()
                .dojangCode(dojangCode)
                .dojangId(reqDto.getDojangId())
                .dojangPw(encodedPassword)
                .dojangName(reqDto.getDojangName())
                .masterName(reqDto.getMasterName())
                .masterBirth(reqDto.getMasterBirth())
                .masterPhone(reqDto.getMasterPhone())
                .dojangTel(reqDto.getDojangTel())
                .dojangEmail(reqDto.getDojangEmail())
                .dojangBizNum(reqDto.getDojangBizNum())
                .dojangZipcode(reqDto.getDojangZipcode())
                .dojangAdd(reqDto.getDojangAdd())
                .dojangAdd2(reqDto.getDojangAdd2())
                .selectedSchool(reqDto.getSelectedSchool())
                .selectedSchool2(reqDto.getSelectedSchool2())
                .note(reqDto.getNote())
                .dojangStatus("운영")  // 기본값
                .isDeleted(0)  // 기본값
                .approvalYn(0)  // 기본값: 미승인
                .build();
        
        // 5. DB 저장 (taekwondo_mst)
        Dojang savedDojang = dojangRepository.save(dojang);
        
        // 6. signup_alarm 테이블에도 저장
        SignupAlarm signupAlarm = SignupAlarm.builder()
                .dojangCode(savedDojang.getDojangCode())
                .dojangName(savedDojang.getDojangName())
                .masterName(savedDojang.getMasterName())
                .masterPhone(savedDojang.getMasterPhone())
                .approvalStatus(0)  // 미승인
                .isRead(0)  // 안읽음
                .build();
        
        signupAlarmRepository.save(signupAlarm);
        
        log.info("새로운 도장 회원가입 완료: {} ({})", savedDojang.getDojangName(), savedDojang.getDojangCode());
        log.info("가입 신청 알림 생성 완료");
        
        // 7. 응답 DTO 생성
        return RegisterRespDto.builder()
                .dojangCode(savedDojang.getDojangCode())
                .dojangId(savedDojang.getDojangId())
                .dojangName(savedDojang.getDojangName())
                .masterName(savedDojang.getMasterName())
                .approvalYn(savedDojang.getApprovalYn())
                .build();
    }
    
    /**
     * 아이디 중복 확인
     */
    public CheckIdRespDto checkId(String dojangId) {
        boolean exists = dojangRepository.existsByDojangId(dojangId);
        
        return CheckIdRespDto.builder()
                .available(!exists)  // 중복이 아니면 true (사용 가능)
                .dojangId(dojangId)
                .build();
    }
    
    /**
     * 로그인
     */
    @Transactional
    public LoginRespDto login(LoginReqDto reqDto) {
        
        // 1. 아이디로 도장 조회
        Dojang dojang = dojangRepository.findByDojangId(reqDto.getDojangId())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));
        
        // 2. 삭제된 도장 체크
        if (dojang.getIsDeleted() == 1) {
            throw new IllegalArgumentException("탈퇴한 계정입니다.");
        }
        
        // 3. 비밀번호 확인
        if (!passwordEncoder.matches(reqDto.getDojangPw(), dojang.getDojangPw())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        
        // 4. 승인여부 확인
        if (dojang.getApprovalYn() == 0) {
            throw new IllegalArgumentException("관리자 승인후 로그인이 가능합니다.");
        }
        
        // 5. 마지막 로그인 시간 업데이트
        dojang.setLastLoginAt(LocalDateTime.now());
        dojangRepository.save(dojang);
        
        log.info("도장 로그인: {} ({})", dojang.getDojangName(), dojang.getDojangCode());
        
        // 6. JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(
                dojang.getDojangCode(),
                dojang.getDojangId(),
                dojang.getDojangName()
        );
        
        String refreshToken = jwtUtil.createRefreshToken(
                dojang.getDojangCode(),
                dojang.getDojangId()
        );
        
        // 7. 응답 DTO 생성
        return LoginRespDto.builder()
                .dojangCode(dojang.getDojangCode())
                .dojangId(dojang.getDojangId())
                .dojangName(dojang.getDojangName())
                .masterName(dojang.getMasterName())
                .approvalYn(dojang.getApprovalYn())
                .dojangStatus(dojang.getDojangStatus())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
    
    /**
     * 아이디 찾기
     */
    public FindIdRespDto findId(FindIdReqDto reqDto) {
        
        // 도장명과 이메일로 도장 찾기
        Dojang dojang = dojangRepository.findByDojangNameAndDojangEmailAndIsDeleted(
                reqDto.getDojangName(), 
                reqDto.getDojangEmail(), 
                0
        ).orElseThrow(() -> new IllegalArgumentException("일치하는 정보가 없습니다."));
        
        log.info("아이디 찾기 성공: {}", dojang.getDojangId());
        
        return FindIdRespDto.builder()
                .dojangId(dojang.getDojangId())
                .dojangName(dojang.getDojangName())
                .build();
    }
    
    /**
     * 비밀번호 찾기 (초기화)
     */
    @Transactional
    public ResetPwRespDto resetPassword(ResetPwReqDto reqDto) {
        
        // 아이디, 도장명, 이메일로 도장 찾기
        Dojang dojang = dojangRepository.findByDojangIdAndDojangNameAndDojangEmailAndIsDeleted(
                reqDto.getDojangId(),
                reqDto.getDojangName(),
                reqDto.getDojangEmail(),
                0
        ).orElseThrow(() -> new IllegalArgumentException("일치하는 정보가 없습니다."));
        
        // 임시 비밀번호 설정 (mw12345)
        String tempPassword = "mw12345";
        String encodedPassword = passwordEncoder.encode(tempPassword);
        
        dojang.setDojangPw(encodedPassword);
        dojangRepository.save(dojang);
        
        log.info("비밀번호 초기화 완료: {}", dojang.getDojangId());
        
        return ResetPwRespDto.builder()
                .dojangId(dojang.getDojangId())
                .tempPassword(tempPassword)
                .build();
    }
    
    /**
     * 토큰 재발급
     */
    public RefreshTokenRespDto refreshToken(RefreshTokenReqDto reqDto) {
        
        String refreshToken = reqDto.getRefreshToken();
        
        // 1. Refresh Token 유효성 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }
        
        // 2. Refresh Token에서 정보 추출
        String dojangCode = jwtUtil.getDojangCode(refreshToken);
        
        // 3. 도장 정보 조회
        Dojang dojang = dojangRepository.findByDojangCode(dojangCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 도장입니다."));
        
        // 4. 새로운 토큰 생성
        String newAccessToken = jwtUtil.createAccessToken(
                dojang.getDojangCode(),
                dojang.getDojangId(),
                dojang.getDojangName()
        );
        
        String newRefreshToken = jwtUtil.createRefreshToken(
                dojang.getDojangCode(),
                dojang.getDojangId()
        );
        
        log.info("토큰 재발급 완료: {}", dojang.getDojangName());
        
        return RefreshTokenRespDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
    
    /**
     * 도장 코드 생성 (MW + 연도2자리 + 순차번호3자리)
     * 예: MW26001, MW26002
     */
    private String generateDojangCode() {
        // 1. 현재 연도 뒤 2자리 추출 (예: 2026 -> "26")
        String yearSuffix = String.valueOf(LocalDate.now().getYear()).substring(2);
        String prefix = "MW" + yearSuffix;

        // 2. 해당 연도로 시작하는 코드 중 가장 높은 번호 조회
        // 예: "MW26"으로 시작하는 코드 중 가장 큰 값을 가져옴
        Optional<Dojang> latestDojang = dojangRepository.findFirstByDojangCodeStartingWithOrderByDojangCodeDesc(prefix);

        int nextSequence = 1; // 기본 순번

        if (latestDojang.isPresent()) {
            String latestCode = latestDojang.get().getDojangCode();
            try {
                // "MW26005" -> "005" 부분만 추출하여 숫자로 변환
                String sequencePart = latestCode.substring(4); 
                nextSequence = Integer.parseInt(sequencePart) + 1;
            } catch (Exception e) {
                // 파싱 에러 시 기본값 유지 또는 예외 처리
                nextSequence = 1;
            }
        }

        // 3. MW + YY + 3자리 숫자(0으로 채움) 형식으로 반환
        return String.format("%s%03d", prefix, nextSequence);
    }
}