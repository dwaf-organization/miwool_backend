package com.taekwondo.miwool.service.admin;

import com.taekwondo.miwool.dto.admin.student.respDto.BulkUploadRespDto;
import com.taekwondo.miwool.dto.student.reqDto.RegisterStudentReqDto;
import com.taekwondo.miwool.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class AdminStudentBulkService {

    @Autowired
    private StudentService studentService;

    /**
     * 엑셀 파일을 파싱하여 제자 대량 등록
     */
    public BulkUploadRespDto bulkUploadStudents(MultipartFile file, String dojangCode) {
        log.info("제자 대량 등록 시작: dojangCode={}", dojangCode);
        
        List<BulkUploadRespDto.BulkUploadErrorDto> errors = new ArrayList<>();
        int totalCount = 0;
        int successCount = 0;
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트
            
            // 4행부터 데이터 시작 (index 3)
            int startRow = 3;
            int lastRow = sheet.getLastRowNum();
            
            log.info("엑셀 파싱: 총 {}행 (데이터: {}행부터 {}행까지)", 
                    lastRow + 1, startRow + 1, lastRow + 1);
            
            for (int rowIndex = startRow; rowIndex <= lastRow; rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                
                // 빈 행 스킵
                if (row == null || isEmptyRow(row)) {
                    continue;
                }
                
                totalCount++;
                int excelRowNumber = rowIndex + 1; // 엑셀 행 번호 (1부터 시작)
                
                try {
                    // 행 데이터를 DTO로 변환
                    RegisterStudentReqDto reqDto = parseRowToDto(row, dojangCode);
                    
                    // 제자 등록
                    studentService.registerStudent(dojangCode, reqDto);
                    
                    successCount++;
                    log.debug("제자 등록 성공: row={}, name={}", excelRowNumber, reqDto.getStudentName());
                    
                } catch (Exception e) {
                    log.error("제자 등록 실패: row={}, error={}", excelRowNumber, e.getMessage());
                    
                    String studentName = getCellValueAsString(row.getCell(0)); // A열: 제자명
                    errors.add(BulkUploadRespDto.BulkUploadErrorDto.builder()
                            .row(excelRowNumber)
                            .studentName(studentName)
                            .error(e.getMessage())
                            .build());
                }
            }
            
        } catch (IOException e) {
            log.error("엑셀 파일 읽기 실패", e);
            throw new IllegalArgumentException("엑셀 파일을 읽을 수 없습니다: " + e.getMessage());
        }
        
        int failCount = totalCount - successCount;
        
        log.info("제자 대량 등록 완료: 총={}건, 성공={}건, 실패={}건", 
                totalCount, successCount, failCount);
        
        return BulkUploadRespDto.builder()
                .totalCount(totalCount)
                .successCount(successCount)
                .failCount(failCount)
                .errors(errors)
                .build();
    }

    /**
     * 엑셀 행을 RegisterStudentReqDto로 변환
     */
    private RegisterStudentReqDto parseRowToDto(Row row, String dojangCode) {
        RegisterStudentReqDto dto = new RegisterStudentReqDto();
        
        // 컬럼 순서에 맞춰 데이터 추출
        dto.setStudentName(getCellValueAsString(row.getCell(0)));           // A: 제자명*
        dto.setRegistDate(getCellValueAsLocalDate(row.getCell(1)));         // B: 등록일*
        dto.setBeltCode(getCellValueAsString(row.getCell(2)));              // C: 급수코드*
        dto.setTaekwondoMonths(getCellValueAsInteger(row.getCell(3)));      // D: 태권도경력(개월)*
        dto.setBirthDate(getCellValueAsLocalDate(row.getCell(4)));          // E: 생년월일*
        dto.setGenderCode(getCellValueAsInteger(row.getCell(5)));           // F: 성별코드*
        dto.setStudentPhone(getCellValueAsString(row.getCell(6)));          // G: 제자연락처
        dto.setStudentZipcode(getCellValueAsString(row.getCell(7)));        // H: 우편번호
        dto.setStudentAdd(getCellValueAsString(row.getCell(8)));            // I: 주소
        dto.setStudentAdd2(getCellValueAsString(row.getCell(9)));           // J: 상세주소
        dto.setSchoolName(getCellValueAsString(row.getCell(10)));           // K: 학교명*
        dto.setGrade(getCellValueAsString(row.getCell(11)));                // L: 학년*
        dto.setClassName(getCellValueAsString(row.getCell(12)));            // M: 반
        dto.setHasExerciseHistory(getCellValueAsInteger(row.getCell(13)));  // N: 운동경력유무*
        dto.setPreviousSports(getCellValueAsString(row.getCell(14)));       // O: 이전운동종목
        dto.setPreviousDojangExp(getCellValueAsString(row.getCell(15)));    // P: 이전도장경험
        dto.setRegistPathCode(getCellValueAsString(row.getCell(16)));       // Q: 등록경로코드*
        
        // R: 목적코드 (콤마 구분)
        String purposeCodesStr = getCellValueAsString(row.getCell(17));
        if (purposeCodesStr != null && !purposeCodesStr.trim().isEmpty()) {
            List<String> purposeCodes = Arrays.asList(purposeCodesStr.split(","));
            dto.setPurposeCodes(purposeCodes);
        }
        
        dto.setPurposeEtcValue(getCellValueAsString(row.getCell(18)));      // S: 목적기타값
        dto.setRegistReason(getCellValueAsString(row.getCell(19)));         // T: 등록사유
        dto.setHasMedication(getCellValueAsInteger(row.getCell(20)));       // U: 복약유무*
        
        // V: 복약상세 → healthNote로 매핑
        String medicationDetail = getCellValueAsString(row.getCell(21));
        dto.setHealthNote(medicationDetail);
        
        dto.setHasAllergy(getCellValueAsInteger(row.getCell(22)));          // W: 알러지유무*
        dto.setGuardianName(getCellValueAsString(row.getCell(23)));         // X: 보호자명*
        dto.setGuardianRelationship(getCellValueAsString(row.getCell(24))); // Y: 보호자관계*
        dto.setGuardianPhone(getCellValueAsString(row.getCell(25)));        // Z: 보호자연락처*
        dto.setGuardianEmergencyPhone(getCellValueAsString(row.getCell(26))); // AA: 보호자비상연락처
        dto.setGuardianBirthDate(getCellValueAsLocalDate(row.getCell(27))); // AB: 보호자생년월일
        dto.setGuardianJob(getCellValueAsString(row.getCell(28)));          // AC: 보호자직업
        dto.setGuardianAnniversaryDate(getCellValueAsLocalDate(row.getCell(29))); // AD: 보호자기념일
        dto.setGuardianRequest(getCellValueAsString(row.getCell(30)));      // AE: 보호자요청사항
        
        return dto;
    }

    /**
     * 셀 값을 String으로 변환
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    /**
     * 셀 값을 Integer로 변환
     */
    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                String value = cell.getStringCellValue().trim();
                if (value.isEmpty()) {
                    return null;
                }
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return null;
                }
            default:
                return null;
        }
    }

    /**
     * 셀 값을 LocalDate로 변환
     */
    private LocalDate getCellValueAsLocalDate(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    return date.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                } else {
                    return null;
                }
            case STRING:
                String dateStr = cell.getStringCellValue().trim();
                if (dateStr.isEmpty()) {
                    return null;
                }
                try {
                    return LocalDate.parse(dateStr);
                } catch (Exception e) {
                    return null;
                }
            default:
                return null;
        }
    }

    /**
     * 빈 행 체크
     */
    private boolean isEmptyRow(Row row) {
        for (int i = 0; i < 31; i++) { // 31개 컬럼 체크
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}