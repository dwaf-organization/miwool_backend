package com.taekwondo.miwool.controller;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.student.reqDto.CreateBeltHistoryReqDto;
import com.taekwondo.miwool.dto.student.reqDto.RegisterStudentReqDto;
import com.taekwondo.miwool.dto.student.reqDto.SaveCharacterTraitReqDto;
import com.taekwondo.miwool.dto.student.reqDto.StudentListReqDto;
import com.taekwondo.miwool.dto.student.reqDto.UpdateStudentBasicInfoReqDto;
import com.taekwondo.miwool.dto.student.respDto.BeltHistoryListRespDto;
import com.taekwondo.miwool.dto.student.respDto.CharacterTraitInfoRespDto;
import com.taekwondo.miwool.dto.student.respDto.RegisterStudentRespDto;
import com.taekwondo.miwool.dto.student.respDto.StudentBasicInfoRespDto;
import com.taekwondo.miwool.dto.student.respDto.StudentListRespDto;
import com.taekwondo.miwool.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {
    
    private final StudentService studentService;
    
    /**
     * 제자 등록 (Step 1)
     * POST /api/v1/students/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(
            @AuthenticationPrincipal String dojangCode,
            @Valid @RequestBody RegisterStudentReqDto reqDto,
            BindingResult bindingResult) {
        
        // Validation 에러 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(errorMessage));
        }
        
        try {
            RegisterStudentRespDto respDto = studentService.registerStudent(dojangCode, reqDto);
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("제자 등록이 완료되었습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.warn("제자 등록 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("제자 등록 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("제자 등록 처리 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 제자 목록 조회
     * GET /api/v1/students
     */
    @GetMapping
    public ResponseEntity<?> getStudentList(
            @AuthenticationPrincipal String dojangCode,
            @RequestParam(value = "studentSearch", required = false) String studentSearch,
            @RequestParam(value = "beltCode", required = false) String beltCode,
            @RequestParam(value = "genderCode", required = false) String genderCodeParam,
            @RequestParam(value = "gradeCode", required = false) String gradeCode,
            @RequestParam(value = "statusCode", required = false) String statusCode) {
        
        try {
            // 파라미터 전처리: 빈 문자열 또는 "전체"를 null로 변환
            studentSearch = normalizeParam(studentSearch);
            beltCode = normalizeParam(beltCode);
            gradeCode = normalizeParam(gradeCode);
            statusCode = normalizeParam(statusCode);
            
            // genderCode 전처리 (Integer 변환)
            Integer genderCode = normalizeGenderCode(genderCodeParam);
            
            StudentListReqDto reqDto = StudentListReqDto.builder()
                    .studentSearch(studentSearch)
                    .beltCode(beltCode)
                    .genderCode(genderCode)
                    .gradeCode(gradeCode)
                    .statusCode(statusCode)
                    .build();
            
            List<StudentListRespDto> response = studentService.getStudentList(dojangCode, reqDto);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("제자 목록 조회 성공", response));
            
        } catch (Exception e) {
            log.error("제자 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("제자 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 파라미터 정규화: 빈 문자열 또는 "전체"를 null로 변환
     */
    private String normalizeParam(String param) {
        if (param == null || param.trim().isEmpty() || "전체".equals(param.trim())) {
            return null;
        }
        return param.trim();
    }
    
    /**
     * genderCode 정규화: 빈 문자열 또는 "전체"를 null로 변환, 숫자는 Integer로 변환
     */
    private Integer normalizeGenderCode(String genderCodeParam) {
        String normalized = normalizeParam(genderCodeParam);
        if (normalized == null) {
            return null;
        }
        try {
            return Integer.parseInt(normalized);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 제자 기본정보 조회
     * GET /api/v1/students/basic-info?studentCode={studentCode}
     */
    @GetMapping("/basic-info")
    public ResponseEntity<?> getStudentBasicInfo(
            @RequestParam("studentCode") String studentCode) {
        
        try {
            StudentBasicInfoRespDto respDto = studentService.getStudentBasicInfo(studentCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("제자 기본정보 조회 성공", respDto));
            
        } catch (IllegalArgumentException e) {
            log.error("제자 기본정보 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("제자 기본정보 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("제자 기본정보 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 제자 기본정보 수정
     * PUT /api/v1/students/basic-info
     */
    @PutMapping("/basic-info/update")
    public ResponseEntity<?> updateStudentBasicInfo(
            @AuthenticationPrincipal String dojangCode,
            @Valid @RequestBody UpdateStudentBasicInfoReqDto reqDto,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            studentService.updateStudentBasicInfo(dojangCode, reqDto);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("제자 기본정보 수정 성공", null));
            
        } catch (IllegalArgumentException e) {
            log.error("제자 기본정보 수정 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("제자 기본정보 수정 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("제자 기본정보 수정 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 제자 성향 정보 조회
     * GET /api/v1/students/character-info?studentCode={studentCode}
     */
    @GetMapping("/character-info")
    public ResponseEntity<?> getCharacterTraitInfo(
            @RequestParam("studentCode") String studentCode) {
        
        try {
            CharacterTraitInfoRespDto respDto = studentService.getCharacterTraitInfo(studentCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("제자 성향 정보 조회 성공", respDto));
            
        } catch (IllegalArgumentException e) {
            log.error("제자 성향 정보 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("제자 성향 정보 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("제자 성향 정보 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 제자 성향 정보 저장
     * PUT /api/v1/students/character-info
     */
    @PutMapping("/character-info")
    public ResponseEntity<?> saveCharacterTraitInfo(
            @Valid @RequestBody SaveCharacterTraitReqDto reqDto,
            BindingResult bindingResult) {
        
        // Validation 체크
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            studentService.saveCharacterTraitInfo(reqDto);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("제자 성향 정보 저장 성공", null));
            
        } catch (IllegalArgumentException e) {
            log.error("제자 성향 정보 저장 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("제자 성향 정보 저장 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("제자 성향 정보 저장 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 급수/경력 이력 조회
     * GET /api/v1/students/belt-history
     */
    @GetMapping("/belt-history")
    public ResponseEntity<?> getBeltHistory(
            @RequestParam("studentCode") String studentCode) {
        
        try {
            BeltHistoryListRespDto respDto = studentService.getBeltHistory(studentCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("급수/경력 이력을 조회했습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.error("급수/경력 이력 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("급수/경력 이력 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("급수/경력 이력 조회 중 오류가 발생했습니다."));
        }
    }
     
    /**
     * 급수/경력 이력 등록
     * POST /api/v1/students/belt-history/create
     */
    @PostMapping("/belt-history/create")
    public ResponseEntity<?> createBeltHistory(
            @Valid @RequestBody CreateBeltHistoryReqDto reqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal String dojangCode) {
        
        // Validation 체크
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail("입력값이 올바르지 않습니다."));
        }
        
        try {
            studentService.createBeltHistory(reqDto, dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("급수/경력 이력을 등록했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("급수/경력 이력 등록 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("급수/경력 이력 등록 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("급수/경력 이력 등록 중 오류가 발생했습니다."));
        }
    }
     
    /**
     * 급수/경력 이력 삭제
     * DELETE /api/v1/students/belt-history
     */
    @DeleteMapping("/belt-history")
    public ResponseEntity<?> deleteBeltHistory(
            @RequestParam("historyCode") String historyCode) {
        
        try {
            studentService.deleteBeltHistory(historyCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("급수/경력 이력을 삭제했습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("급수/경력 이력 삭제 실패: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("급수/경력 이력 삭제 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("급수/경력 이력 삭제 중 오류가 발생했습니다."));
        }
    }
    
}