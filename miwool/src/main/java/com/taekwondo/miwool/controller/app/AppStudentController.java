package com.taekwondo.miwool.controller.app;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.app.student.reqDto.ProfileImageUpdateReqDto;
import com.taekwondo.miwool.dto.app.student.respDto.StudentDetailRespDto;
import com.taekwondo.miwool.dto.app.student.respDto.StudentListRespDto;
import com.taekwondo.miwool.service.app.AppStudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/app/students")
@RequiredArgsConstructor
public class AppStudentController {

    private final AppStudentService appStudentService;

    // 앱 제자 프로필 이미지 업데이트
    @PutMapping("/profile-image")
    public ResponseEntity<?> updateProfileImage(@RequestBody ProfileImageUpdateReqDto reqDto) {
        
        try {
            appStudentService.updateProfileImage(
                    reqDto.getDojangCode(),
                    reqDto.getStudentCode(),
                    reqDto.getProfileImageUrl());
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("프로필 이미지를 업데이트했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("앱 제자 프로필 이미지 업데이트 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("프로필 이미지 업데이트 중 오류가 발생했습니다."));
        }
    }

    // 앱 제자 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<?> getStudentDetail(
            @RequestParam(value = "dojangCode", required = true) String dojangCode,
            @RequestParam(value = "studentCode", required = true) String studentCode) {
        
        try {
            StudentDetailRespDto respDto = appStudentService.getStudentDetail(dojangCode, studentCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("제자 상세 정보를 조회했습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("앱 제자 상세 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("제자 상세 조회 중 오류가 발생했습니다."));
        }
    }

    // 앱 제자 목록 조회
    @GetMapping("/list")
    public ResponseEntity<?> getStudentList(
            @RequestParam(value = "dojangCode", required = true) String dojangCode,
            @RequestParam(value = "studentSearch", required = false, defaultValue = "") String studentSearch,
            @RequestParam(value = "grade", required = false, defaultValue = "전체") String grade,
            @RequestParam(value = "genderCode", required = false, defaultValue = "전체") String genderCode,
            @RequestParam(value = "beltCode", required = false, defaultValue = "전체") String beltCode,
            @RequestParam(value = "statusCode", required = false, defaultValue = "전체") String statusCode,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        
        try {
            StudentListRespDto respDto = appStudentService.getStudentList(
                    dojangCode,
                    studentSearch,
                    grade,
                    genderCode,
                    beltCode,
                    statusCode,
                    page,
                    size);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("제자 목록을 조회했습니다.", respDto));
            
        } catch (Exception e) {
            log.error("앱 제자 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("제자 목록 조회 중 오류가 발생했습니다."));
        }
    }
}