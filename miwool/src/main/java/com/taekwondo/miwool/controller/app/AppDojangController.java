package com.taekwondo.miwool.controller.app;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.app.mypage.respDto.DojangInfoRespDto;
import com.taekwondo.miwool.service.app.AppDojangService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/app/mypage")
@RequiredArgsConstructor
public class AppDojangController {

    private final AppDojangService appDojangService;

    // 앱 도장정보 조회
    @GetMapping("/dojang")
    public ResponseEntity<?> getDojangInfo(
            @RequestParam(value = "dojangCode", required = true) String dojangCode) {
        
        try {
            DojangInfoRespDto respDto = appDojangService.getDojangInfo(dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("도장정보를 조회했습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.error("앱 도장정보 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("앱 도장정보 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("도장정보 조회 중 오류가 발생했습니다."));
        }
    }
}