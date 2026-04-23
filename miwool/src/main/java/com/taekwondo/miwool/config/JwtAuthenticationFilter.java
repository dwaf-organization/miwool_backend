package com.taekwondo.miwool.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.taekwondo.miwool.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 1. Request Header에서 JWT 토큰 추출
            String token = resolveToken(request);
            
            // 2. 토큰 유효성 검증
            if (token != null && jwtUtil.validateToken(token)) {
                // 3. 토큰에서 도장 정보 추출
                String dojangCode = jwtUtil.getDojangCode(token);
                String dojangName = jwtUtil.getDojangName(token);
                
                // 4. Spring Security에 도장 정보 저장
                // Principal에 dojangCode 저장 (컨트롤러에서 @AuthenticationPrincipal로 접근 가능)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(dojangCode, null, Collections.emptyList());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Security Context에 '{}' 인증 정보 저장, uri: {}", dojangName, request.getRequestURI());
            }
        } catch (Exception e) {
            log.error("Security Context에 도장 인증 정보를 저장할 수 없습니다: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Request Header에서 토큰 정보 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}