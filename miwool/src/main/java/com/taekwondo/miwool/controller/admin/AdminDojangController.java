package com.taekwondo.miwool.controller.admin;

import com.taekwondo.miwool.common.dto.RespDto;
import com.taekwondo.miwool.dto.admin.dojang.reqDto.ApproveReqDto;
import com.taekwondo.miwool.dto.admin.dojang.reqDto.DojangUpdateReqDto;
import com.taekwondo.miwool.dto.admin.dojang.reqDto.VipPackageBulkUpdateReqDto;
import com.taekwondo.miwool.dto.admin.dojang.respDto.DojangActivityRespDto;
import com.taekwondo.miwool.dto.admin.dojang.respDto.DojangDetailRespDto;
import com.taekwondo.miwool.dto.admin.dojang.respDto.DojangItemDto;
import com.taekwondo.miwool.dto.admin.dojang.respDto.DojangOperationRespDto;
import com.taekwondo.miwool.dto.admin.dojang.respDto.PackagesClassesRespDto;
import com.taekwondo.miwool.dto.admin.dojang.respDto.VipPackageBulkUpdateRespDto;
import com.taekwondo.miwool.service.admin.AdminDojangActivityService;
import com.taekwondo.miwool.service.admin.AdminDojangOperationService;
import com.taekwondo.miwool.service.admin.AdminDojangPackageClassService;
import com.taekwondo.miwool.service.admin.AdminDojangService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/dojangs")
@RequiredArgsConstructor
public class AdminDojangController {

    private final AdminDojangService adminDojangService;
    private final AdminDojangOperationService adminDojangOperationService;
    private final AdminDojangActivityService adminDojangActivityService;
    private final AdminDojangPackageClassService adminDojangPackageClassService;

    /**
     * 관리자 도장 목록 조회
     * GET /api/v1/admin/dojangs?dojangSearch=강남&dojangStatus=운영&approvalYn=1&vipPackage=경영
     */
    @GetMapping
    public ResponseEntity<?> getDojangs(
            @RequestParam(value = "dojangSearch", required = false) String dojangSearch,
            @RequestParam(value = "dojangStatus", defaultValue = "전체") String dojangStatus,
            @RequestParam(value = "approvalYn", defaultValue = "전체") String approvalYn,
            @RequestParam(value = "vipPackage", defaultValue = "전체") String vipPackage) {
        
        try {
            List<DojangItemDto> dojangs = adminDojangService.getDojangs(
                    dojangSearch, dojangStatus, approvalYn, vipPackage);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("도장 목록을 조회했습니다.", dojangs));
            
        } catch (Exception e) {
            log.error("관리자 도장 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("도장 목록 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 도장 운영현황 조회
     * GET /api/v1/admin/dojangs/operation?dojangCode=MW26001&month=2026-04
     */
    @GetMapping("/operation")
    public ResponseEntity<?> getDojangOperation(
            @RequestParam(value = "dojangCode") String dojangCode,
            @RequestParam(value = "month") String month) {
        
        try {
            DojangOperationRespDto operation = adminDojangOperationService.getDojangOperation(
                    dojangCode, month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("도장 운영현황을 조회했습니다.", operation));
            
        } catch (Exception e) {
            log.error("도장 운영현황 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("도장 운영현황 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 도장 상세 정보 조회
     * GET /api/v1/admin/dojangs/detail?dojangCode=MW26001
     */
    @GetMapping("/detail")
    public ResponseEntity<?> getDojangDetail(
            @RequestParam(value = "dojangCode") String dojangCode) {
        
        try {
            DojangDetailRespDto dojang = adminDojangService.getDojangDetail(dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("도장 정보를 조회했습니다.", dojang));
            
        } catch (IllegalArgumentException e) {
            log.error("도장 상세 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("도장 상세 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("도장 정보 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 도장 정보 수정
     * PUT /api/v1/admin/dojangs/update
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateDojang(@RequestBody DojangUpdateReqDto reqDto) {
        
        try {
            adminDojangService.updateDojang(reqDto);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("도장 정보가 수정되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("도장 정보 수정 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("도장 정보 수정 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("도장 정보 수정 중 오류가 발생했습니다."));
        }
    }

    /**
     * 비밀번호 초기화
     * PUT /api/v1/admin/dojangs/reset-password/{dojangCode}
     */
    @PutMapping("/reset-password/{dojangCode}")
    public ResponseEntity<?> resetPassword(
            @PathVariable(value = "dojangCode") String dojangCode) {
        
        try {
            adminDojangService.resetPassword(dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("비밀번호가 초기화되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("비밀번호 초기화 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("비밀번호 초기화 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("비밀번호 초기화 중 오류가 발생했습니다."));
        }
    }

    /**
     * 도장 삭제 (소프트 삭제)
     * DELETE /api/v1/admin/dojangs/delete/{dojangCode}
     */
    @DeleteMapping("/delete/{dojangCode}")
    public ResponseEntity<?> deleteDojang(
            @PathVariable(value = "dojangCode") String dojangCode) {
        
        try {
            adminDojangService.deleteDojang(dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("도장이 삭제되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("도장 삭제 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("도장 삭제 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("도장 삭제 중 오류가 발생했습니다."));
        }
    }

    /**
     * 도장 승인 처리
     * POST /api/v1/admin/dojangs/approve
     */
    @PostMapping("/approve")
    public ResponseEntity<?> approveDojang(@RequestBody ApproveReqDto reqDto) {
        
        try {
            adminDojangService.approveDojang(reqDto.getDojangCode());
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("도장 승인이 완료되었습니다.", null));
            
        } catch (IllegalArgumentException e) {
            log.error("도장 승인 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("도장 승인 처리 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("도장 승인 처리 중 오류가 발생했습니다."));
        }
    }

    /**
     * VIP 패키지 일괄 저장
     * PUT /api/v1/admin/dojangs/vip
     */
    @PutMapping("/vip")
    public ResponseEntity<?> updateVipPackages(@RequestBody VipPackageBulkUpdateReqDto reqDto) {
        
        try {
            VipPackageBulkUpdateRespDto respDto = adminDojangService.updateVipPackages(reqDto);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("VIP 패키지가 일괄 저장되었습니다.", respDto));
            
        } catch (IllegalArgumentException e) {
            log.error("VIP 패키지 일괄 저장 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (RuntimeException e) {
            log.error("VIP 패키지 일괄 저장 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("VIP 패키지 일괄 저장 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("VIP 패키지 일괄 저장 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 도장 월별 활동현황 조회
     * GET /api/v1/admin/dojangs/activity?dojangCode={dojangCode}&month={month}
     */
    @GetMapping("/activity")
    public ResponseEntity<?> getDojangActivity(
            @RequestParam(value = "dojangCode") String dojangCode,
            @RequestParam(value = "month") String month) {
        
        try {
            log.info("도장 활동현황 조회 요청: dojangCode={}, month={}", dojangCode, month);
            
            DojangActivityRespDto data = adminDojangActivityService.getDojangActivity(dojangCode, month);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("활동현황을 조회했습니다.", data));
            
        } catch (IllegalArgumentException e) {
            log.error("도장 활동현황 조회 실패: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(RespDto.fail(e.getMessage()));
            
        } catch (Exception e) {
            log.error("도장 활동현황 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("도장 활동현황 조회 중 오류가 발생했습니다."));
        }
    }
    
    /**
     * 패키지 및 수업 목록 조회
     * GET /api/v1/admin/dojangs/packages-classes?dojangCode={dojangCode}
     */
    @GetMapping("/packages-classes")
    public ResponseEntity<?> getPackagesAndClasses(
            @RequestParam(value = "dojangCode") String dojangCode) {
        
        try {
            log.info("패키지 및 수업 목록 조회 요청: dojangCode={}", dojangCode);
            
            PackagesClassesRespDto data = adminDojangPackageClassService.getPackagesAndClasses(dojangCode);
            
            return ResponseEntity
                    .ok()
                    .body(RespDto.success("패키지 및 수업 목록을 조회했습니다.", data));
            
        } catch (Exception e) {
            log.error("패키지 및 수업 목록 조회 중 오류 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespDto.fail("패키지 및 수업 목록 조회 중 오류가 발생했습니다."));
        }
    }
    
}