package com.taekwondo.miwool.controller.admin;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.admin.student.respDto.BulkUploadRespDto;
import com.taekwondo.miwool.service.admin.AdminStudentBulkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/students")
public class AdminStudentBulkController {

    @Autowired
    private AdminStudentBulkService adminStudentBulkService;

    /**
     * 제자 대량 등록 (엑셀 업로드)
     * POST /api/v1/admin/students/bulk-upload
     */
    @PostMapping("/bulk-upload")
    public ResponseEntity<?> bulkUpload(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "dojangCode") String dojangCode) {
        
        log.info("제자 대량 등록 요청: dojangCode={}, filename={}", dojangCode, file.getOriginalFilename());
        
        try {
            // 파일 확장자 검증
            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(RespDto.fail("엑셀 파일(.xlsx, .xls)만 업로드 가능합니다."));
            }
            
            // 파일 크기 검증 (10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(RespDto.fail("파일 크기는 10MB를 초과할 수 없습니다."));
            }
            
            // 빈 파일 검증
            if (file.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(RespDto.fail("빈 파일입니다."));
            }
            
            // 대량 등록 처리
            BulkUploadRespDto result = adminStudentBulkService.bulkUploadStudents(file, dojangCode);
            
            // 결과에 따른 메시지 생성
            String message;
            if (result.getFailCount() == 0) {
                message = String.format("제자 대량 등록이 완료되었습니다. (성공: %d건)", 
                        result.getSuccessCount());
            } else {
                message = String.format("제자 대량 등록이 완료되었습니다. (성공: %d건, 실패: %d건)", 
                        result.getSuccessCount(), result.getFailCount());
            }
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success(message, result));
            
        } catch (IllegalArgumentException e) {
            log.error("제자 대량 등록 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("제자 대량 등록 처리 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("제자 대량 등록 처리 중 오류가 발생했습니다."));
        }
    }

    /**
     * 엑셀 템플릿 다운로드
     * GET /api/v1/admin/students/download-template
     */
    @GetMapping("/download-template")
    public ResponseEntity<byte[]> downloadTemplate() {
        
        log.info("엑셀 템플릿 다운로드 요청");
        
        try {
            // 템플릿 파일 경로
            String templatePath = "/mnt/user-data/outputs/제자_대량_등록_템플릿.xlsx";
            
            // 파일 읽기
            FileInputStream fis = new FileInputStream(templatePath);
            byte[] fileBytes = fis.readAllBytes();
            fis.close();
            
            // 응답 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "제자_대량_등록_템플릿.xlsx");
            
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(fileBytes);
            
        } catch (IOException e) {
            log.error("템플릿 파일 다운로드 실패", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}