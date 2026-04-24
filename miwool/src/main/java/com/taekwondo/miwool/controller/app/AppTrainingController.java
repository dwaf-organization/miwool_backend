package com.taekwondo.miwool.controller.app;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.app.training.reqDto.RegisterTrainingReqDto;
import com.taekwondo.miwool.dto.app.training.respDto.ClassOptionRespDto;
import com.taekwondo.miwool.dto.app.training.respDto.PackageOptionRespDto;
import com.taekwondo.miwool.service.app.AppTrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/app/trainings")
@RequiredArgsConstructor
public class AppTrainingController {

    private final AppTrainingService appTrainingService;

    // 앱 패키지 선택 목록 조회
    @GetMapping("/packages")
    public ResponseEntity<?> getPackageOptions(
            @RequestParam(value = "dojangCode", required = true) String dojangCode) {
        
        try {
            List<PackageOptionRespDto> packages = appTrainingService.getPackageOptions(dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("패키지 선택 목록을 조회했습니다.", packages));
            
        } catch (Exception e) {
            log.error("앱 패키지 선택 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("패키지 선택 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // 앱 수업 선택 목록 조회
    @GetMapping("/classes")
    public ResponseEntity<?> getClassOptions(
            @RequestParam(value = "dojangCode", required = true) String dojangCode) {
        
        try {
            List<ClassOptionRespDto> classes = appTrainingService.getClassOptions(dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("수업 선택 목록을 조회했습니다.", classes));
            
        } catch (Exception e) {
            log.error("앱 수업 선택 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("수업 선택 목록 조회 중 오류가 발생했습니다."));
        }
    }

    // 앱 수련 등록
    @PostMapping
    public ResponseEntity<?> registerTraining(@RequestBody RegisterTrainingReqDto reqDto) {
        
        try {
            appTrainingService.registerTraining(reqDto);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("수련 등록이 완료되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("앱 수련 등록 중 오류: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("앱 수련 등록 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("수련 등록 중 오류가 발생했습니다."));
        }
    }
}