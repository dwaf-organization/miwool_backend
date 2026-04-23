package com.taekwondo.miwool.controller.app;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.common.respDto.CommonCodeRespDto;
import com.taekwondo.miwool.service.app.AppCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/app/common")
@RequiredArgsConstructor
public class AppCommonController {

    private final AppCommonService appCommonService;

    // 앱 공통코드 조회
    @GetMapping("/codes")
    public ResponseEntity<?> getCodesByGroupCode(
            @RequestParam(value = "groupCode", required = true) String groupCode) {
        
        try {
            List<CommonCodeRespDto> codes = appCommonService.getCodesByGroupCode(groupCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("공통코드 조회 성공", codes));
            
        } catch (Exception e) {
            log.error("앱 공통코드 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("공통코드 조회 중 오류가 발생했습니다."));
        }
    }
}